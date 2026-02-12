package com.github.rag.tutorials.helpdesk.application.agent;

import com.github.rag.tutorials.helpdesk.application.agent.dto.TicketCreationResult;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ConversationState;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.RequestMessagePayload;
import com.github.rag.tutorials.helpdesk.domain.conversation.model.ResponseMessagePayload;
import com.github.rag.tutorials.helpdesk.infrastructure.rag.agent.TicketCreationAgent;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketCreationService {
    private final TicketCreationAgent ticketCreationAgent;
    

    public ResponseMessagePayload handleTicketCreation(RequestMessagePayload message, ConversationState state) {
        Result<TicketCreationResult> ticketCreationResult = ticketCreationAgent.createTicket(
                message.getText(),
                state.getCustomerCode(),
                state.getSelectedContractNumber(),
                state.getIssueType() != null ? state.getIssueType().toString() : ""
        );
        
        TicketCreationResult result = ticketCreationResult.content();
        log.info("Ticket creation result: {}", result);

        return ResponseMessagePayload.createWithTicket(result.getMessage(), message, result.getTicketId());
    }
}
