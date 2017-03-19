package com.github.ralfstuckert.junit.jupiter.spring.ticket;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepository extends MongoRepository<Ticket, String> {

	Ticket findByTicketId(final String ticketId);
}
