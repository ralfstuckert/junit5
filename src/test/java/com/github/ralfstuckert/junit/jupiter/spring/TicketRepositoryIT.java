package com.github.ralfstuckert.junit.jupiter.spring;

import com.github.ralfstuckert.junit.jupiter.spring.Application;
import com.github.ralfstuckert.junit.jupiter.spring.ticket.Ticket;
import com.github.ralfstuckert.junit.jupiter.spring.ticket.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.MongoCleanup;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@MongoCleanup(Ticket.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TicketRepositoryIT {

	@Autowired
	private TicketRepository repository;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
    @DisplayName("Test the findsByTicketId() method")
	public void testSaveAndFindTicket() throws Exception {
		Ticket ticket1 = new Ticket("1", "blabla");
		repository.save(ticket1);
		Ticket ticket2 = new Ticket("2", "hihi");
		repository.save(ticket2);

		assertEquals(ticket1, repository.findByTicketId("1"));
		assertEquals(ticket2, repository.findByTicketId("2"));
		assertNull(repository.findByTicketId("3"));
	}

	@Test
    @DisplayName("Ensure that there is a unique index on the ticket ID")
	public void testSaveNewTicketWithExistingTicketId() throws Exception {
        repository.save(new Ticket("1", "blabla"));
	    assertThrows(DuplicateKeyException.class, () -> {
            repository.save(new Ticket("1", "hihi"));
        });
	}

}
