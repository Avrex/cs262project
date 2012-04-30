package edu.harvard.cs262.grading.server.services;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;

public class MongoGradeStorageService implements GradeStorageService {

	private Mongo m;
	private DB db;
	private DBCollection coll;

	public void init() throws UnknownHostException, MongoException {
		m = new Mongo();
		db = m.getDB("dgs");
		coll = db.getCollection("grades"); // change collection name?
	}

	@Override
	public void submitGrade(Submission submission, Grade grade)
			throws RemoteException {
		// XXX: Active question: can Mongo's "put" method really accept /any/
		// objects?
		// How does that work? Surely they must be serializable?

		BasicDBObject info = new BasicDBObject();

		info.put("studentID", submission.getStudent().studentID());
		info.put("assignmentID", submission.getAssignment().assignmentID());
		info.put("grader", grade.getGrader().studentID());
		info.put("score", grade.getScore().getScore());
		info.put("comments", grade.getComments());
		info.put("maxScore", grade.getScore().maxScore());
		info.put("timestamp", grade.getTimeStamp());

		coll.insert(info);

	}

	@Override
	public List<Grade> getGrade(Submission submission) throws RemoteException {

		BasicDBObject query = new BasicDBObject();
		query.put("studentID", submission.getStudent().studentID());
		query.put("assignmentID", submission.getAssignment().assignmentID());

		DBCursor cur = coll.find(query);
		List<Grade> grades = new ArrayList<Grade>();

		while (cur.hasNext()) {
			DBObject gradeObject = cur.next();
			// I'm assuming that, if I put it in as an int, I can pull it out
			// and cast it to Integer????
			Score score = new ScoreImpl((Integer) gradeObject.get("score"),
					(Integer) gradeObject.get("maxScore"));
			Student grader = new StudentImpl((Long) (gradeObject.get("grader")));
			String comments = (String) gradeObject.get("comments");
			Timestamp tm = new Timestamp(
					((Date) gradeObject.get("timestamp")).getTime());
			Grade grade = new GradeImpl(score, grader, comments, tm);
			grades.add(grade);
		}
		return grades;
	}

	public static void main(String[] args) {
		try {
			MongoGradeStorageService obj = new MongoGradeStorageService();
			obj.init();
			GradeStorageService stub = (GradeStorageService) UnicastRemoteObject
					.exportObject(obj, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();

			// check for registry update command
			boolean forceUpdate = false;
			for (int i = 0, len = args.length; i < len; i++)
				if (args[i].equals("--update"))
					forceUpdate = true;

			if (forceUpdate) {
				registry.rebind("GradeStorageService", stub);
			} else {
				registry.bind("GradeStorageService", stub);
			}

			System.err.println("GradeStorageService running");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void heartbeat() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
