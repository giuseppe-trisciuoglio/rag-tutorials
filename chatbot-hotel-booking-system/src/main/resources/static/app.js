const { useState, useEffect, useRef, useCallback } = React;
const URL_REGEX = /(https?:\/\/[^\s]+)/g;
const URL_ONLY_REGEX = /^https?:\/\/[^\s]+$/;

const apiService = {
    async sendChatMessage(message, memoryId) {
        return fetch('/chatbot/message', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message, memoryId }),
        }).then(response => {
            if (!response.ok) throw new Error('Failed to get response from API');
            return response.json();
        });
    },

    async getHistory(memoryId) {
        return fetch(`/message/history?memoryId=${memoryId}`).then(response => {
            if (!response.ok) throw new Error('Failed to fetch chat history');
            return response.json();
        });
    },
};

const NotificationGT = ({ message, type, onClose }) => {
    useEffect(() => {
        const timer = setTimeout(onClose, 3000);
        lucide.createIcons();
        return () => clearTimeout(timer);
    }, [onClose]);

    const bgColor = type === 'error' ? 'bg-red-500' : 'bg-green-500';
    const icon = type === 'error' ? 'alert-circle' : 'check-circle';

    return (
        <div className={`${bgColor} text-white text-sm px-4 py-2 rounded-lg shadow-lg fade-in flex items-center`}>
            <i data-lucide={icon} className="w-4 h-4 mr-2"></i>
            {message}
        </div>
    );
};

function renderLineWithLinks(line, lineIndex) {
    const chunks = line.split(URL_REGEX);
    return chunks.map((chunk, chunkIndex) => {
        if (URL_ONLY_REGEX.test(chunk)) {
            return (
                <a
                    key={`${lineIndex}-${chunkIndex}`}
                    href={chunk}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="underline break-all"
                >
                    {chunk}
                </a>
            );
        }
        return <React.Fragment key={`${lineIndex}-${chunkIndex}`}>{chunk}</React.Fragment>;
    });
}

const ChatGT = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [notification, setNotification] = useState(null);
    const [memoryId, setMemoryId] = useState(() => localStorage.getItem('memoryId') || generateID());

    const messagesEndRef = useRef(null);
    const inputRef = useRef(null);

    useEffect(() => {
        localStorage.setItem('memoryId', memoryId);
    }, [memoryId]);

    const scrollToBottom = useCallback(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, []);

    useEffect(() => {
        const fetchHistory = async () => {
            try {
                const history = await apiService.getHistory(memoryId);
                if (!history.messages) return;
                const formattedHistory = history.messages.map(([text, isUser]) => ({ text, isUser: Boolean(isUser) }));
                setMessages(formattedHistory);
                scrollToBottom();
            } catch (error) {
                console.error('Error fetching chat history:', error);
            } finally {
                lucide.createIcons();
            }
        };

        fetchHistory();
    }, [memoryId, scrollToBottom]);

    useEffect(() => {
        inputRef.current?.focus();
    }, []);

    const sendMessage = useCallback(
        async (e) => {
            e.preventDefault();
            const trimmedInput = input.trim();
            if (!trimmedInput) return;

            setIsLoading(true);
            setMessages((prev) => [...prev, { text: trimmedInput, isUser: true }]);
            setInput('');

            try {
                const data = await apiService.sendChatMessage(trimmedInput, memoryId);
                setMessages((prev) => [...prev, { text: data.responseMessage, isUser: false }]);
            } catch (error) {
                setMessages((prev) => [...prev, { text: error.message, isUser: false }]);
            } finally {
                setIsLoading(false);
                inputRef.current?.focus();
                scrollToBottom();
            }
        },
        [input, memoryId, scrollToBottom]
    );

    const resetChat = useCallback(() => {
        setMemoryId(generateID());
        setMessages([]);
        setInput('');
        setNotification({ message: 'Chat history has been reset.', type: 'success' });
    }, []);

    return (
        <div className="md:w-[600px] w-full mx-auto bg-white rounded-xl overflow-hidden">
            <HeaderGT />
            <ChatWindowGT
                messages={messages}
                isLoading={isLoading}
                sendMessage={sendMessage}
                input={input}
                setInput={setInput}
                inputRef={inputRef}
                messagesEndRef={messagesEndRef}
            />
            <ActionsGT
                resetChat={resetChat}
                notification={notification}
                setNotification={setNotification}
            />
            <FooterGT />
        </div>
    );
};

const HeaderGT = () => (
    <header className="bg-gradient-to-r from-blue-600 to-blue-800 text-white p-6 py-4">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <h1 className="text-xl font-bold">Hotel Booking Assistant</h1>
        </div>
    </header>
);

const ChatWindowGT = React.memo(
    ({ messages, isLoading, sendMessage, input, setInput, inputRef, messagesEndRef }) => (
        <div className="p-6 h-[calc(100vh-240px)] min-h-[300px] flex flex-col">
            <div className="bg-gray-50 rounded-lg border border-gray-200 shadow-inner overflow-hidden flex-1">
                <div className="h-full overflow-y-auto p-4 space-y-4" style={{ scrollBehavior: 'smooth' }}>
                    {messages.length === 0 && (
                        <div className="text-center text-gray-500 mt-4">
                            Start a conversation to check hotel information or manage a reservation.
                        </div>
                    )}
                    {messages.map((msg, index) => (
                        <div key={index} className={`flex ${msg.isUser ? 'justify-end' : 'justify-start'}`}>
                            <div
                                className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg shadow-md ${
                                    msg.isUser
                                        ? 'bg-gradient-to-r from-blue-500 to-blue-600 text-white ml-auto'
                                        : 'bg-white border border-gray-200'
                                } fade-in`}
                            >
                                {msg.text?.split("\n").map((line, index) => (
                                    <span key={index}>
                                        {renderLineWithLinks(line, index)}
                                        <br />
                                    </span>
                                ))}
                            </div>
                        </div>
                    ))}
                    {isLoading && (
                        <div className="flex justify-start">
                            <div className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg typing-indicator">
                                AI is processing
                            </div>
                        </div>
                    )}
                    <div ref={messagesEndRef} />
                </div>
            </div>
            <form onSubmit={sendMessage} className="pt-6 bg-white rounded-b-lg">
                <div className="flex border border-gray-200 rounded-lg overflow-hidden transition-all duration-200">
                    <input
                        ref={inputRef}
                        type="text"
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        placeholder="Type your hotel request..."
                        className="flex-grow px-4 py-2 focus:outline-none"
                        disabled={isLoading}
                    />
                    <button
                        type="submit"
                        className="bg-blue-500 text-white px-6 py-2 hover:bg-blue-600 disabled:opacity-50 transition-all duration-200 flex items-center"
                        disabled={isLoading}
                    >
                        <span>{isLoading ? 'Sending...' : 'Send'}</span>
                        <i data-lucide={isLoading ? 'loader' : 'send'} className="w-4 h-4 ml-2"></i>
                    </button>
                </div>
            </form>
        </div>
    )
);

const ActionsGT = ({ resetChat, notification, setNotification }) => (
    <div className="p-6 pt-0 text-center relative">
        <div className="flex justify-center space-x-4">
            <button
                onClick={resetChat}
                className="bg-gradient-to-r from-red-100 to-red-200
                text-red-500 hover:text-white px-6 py-2 rounded-lg hover:from-red-400 hover:to-red-600
                focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2
                transition-all duration-200 shadow-md flex items-center"
            >
                <span className="text-sm">Clear Chat</span>
                <i data-lucide="trash-2" className="w-4 h-4 ml-2"></i>
            </button>
        </div>
        {notification && (
            <div className="absolute left-1/2 transform -translate-x-1/2 mt-2">
                <NotificationGT message={notification.message} type={notification.type} onClose={() => setNotification(null)} />
            </div>
        )}
    </div>
);

const FooterGT = () => (
    <footer className="bg-gray-100 p-6 py-4 text-center text-sm text-gray-600 border-t border-gray-200">
        <p className="flex items-center justify-center">
            <span>© 2026 Hotel Booking Assistant</span>
        </p>
    </footer>
);

ReactDOM.render(<ChatGT />, document.getElementById('root'));

function generateID() {
    return window.crypto?.randomUUID?.() || Date.now().toString();
}
