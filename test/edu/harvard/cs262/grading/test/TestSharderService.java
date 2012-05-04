package edu.harvard.cs262.grading.test;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.harvard.cs262.grading.server.services.Assignment;
import edu.harvard.cs262.grading.server.services.AssignmentImpl;
import edu.harvard.cs262.grading.server.services.MongoSubmissionStorageService;
import edu.harvard.cs262.grading.server.services.NoShardsForAssignmentException;
import edu.harvard.cs262.grading.server.services.Shard;
import edu.harvard.cs262.grading.server.services.SharderServiceServer;
import edu.harvard.cs262.grading.server.services.StudentImpl;
import edu.harvard.cs262.grading.server.services.Submission;
import edu.harvard.cs262.grading.server.services.SubmissionImpl;

public class TestSharderService {

	@Test
	public void testShard() throws Exception {
		MongoSubmissionStorageService storage = new MongoSubmissionStorageService();
		storage.init();
		SharderServiceServer sharder = new SharderServiceServer();
		sharder.init(true);

		byte[] contents = new byte[42];

		Assignment assn = new AssignmentImpl(0);

		Submission s0 = new SubmissionImpl(new StudentImpl(0), assn, contents);
		Submission s1 = new SubmissionImpl(new StudentImpl(1), assn, contents);

		storage.storeSubmission(s0);
		storage.storeSubmission(s1);

		Shard shard = sharder.generateShard(assn);

		assertTrue(shard.getShard().containsKey(0L));
		assertTrue(shard.getShard().get(0L).contains(1L));
		
		assertTrue(sharder.getShard(shard.shardID()).equals(shard));
		
		assertTrue(sharder.getShardID(assn) == shard.shardID());
		
		try {
			System.out.println(sharder.getShardID(new AssignmentImpl(1874298374L)));
			fail();
		}
		catch (NoShardsForAssignmentException e) {
			assertTrue(true);
		}
		
	}
}
