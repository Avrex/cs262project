package edu.harvard.cs262.grading.server.services;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GradeCompilerServiceServer implements GradeCompilerService {

	private ConfigReader config;

	public GradeCompilerServiceServer() {
		config = new ConfigReaderImpl();
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public Grade storeGrade(Student grader, Submission submission, Score score,
			String comments) throws RemoteException {
		Grade grade = new GradeImpl(score, grader, comments);

		// get GradeStorageService from rmiregistry
		GradeStorageService storage = (GradeStorageService) ServiceLookupUtility
				.lookupService(config, "GradeStorageService");
		if (storage == null) {
			System.err.println("Looking up GradeStorageService failed.");
			return null;
		} else {
			storage.submitGrade(submission, grade);
			return grade;
		}
	}

	@Override
	public Set<Student> getGraders(Submission submission)
			throws RemoteException {

		// list of grades for this submission
		List<Grade> grades = new ArrayList<Grade>();

		// set of graders from the grades
		Set<Student> graders = new HashSet<Student>();

		// get GradeStorageService from rmiregistry
		GradeStorageService storage = (GradeStorageService) ServiceLookupUtility
				.lookupService(config, "GradeStorageService");
		if (storage == null) {
			System.err.println("Looking up GradeStorageService failed.");
		} else {
			grades = storage.getGrade(submission);
		}

		// get the graders from the grades
		for (Grade g : grades)
			graders.add(g.getGrader());

		return graders;
	}

	@Override
	public Map<Submission, List<Grade>> getCompiledGrades(Assignment assignment)
			throws RemoteException {
		// get the list of submissions for this assignment
		Set<Submission> submissions = new HashSet<Submission>();
		SubmissionStorageService storage = (SubmissionStorageService) ServiceLookupUtility
				.lookupService(config, "SubmissionStorageService");
		if (storage == null) {
			System.err.println("Looking up SubmissionStorageService failed.");
		} else {
			submissions = storage.getAllSubmissions(assignment);
		}

		// for each submission, retrieve the list of grades for that submission
		Map<Submission, List<Grade>> grades = new HashMap<Submission, List<Grade>>();
		GradeStorageService gstorage = (GradeStorageService) ServiceLookupUtility
				.lookupService(config, "GradeStorageService");
		if (gstorage == null) {
			System.err.println("Looking up GradeStorageService failed.");
		} else {
			for (Submission s : submissions)
				grades.put(s, gstorage.getGrade(s));
		}

		return grades;

	}

	public static void main(String[] args) {
		try {
			GradeCompilerServiceServer obj = new GradeCompilerServiceServer();
			obj.init();
			GradeCompilerService stub = (GradeCompilerService) UnicastRemoteObject
					.exportObject(obj, 0);

			// bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();

			// check for registry update command
			boolean forceUpdate = false;
			for (int i = 0, len = args.length; i < len; i++)
				if (args[i].equals("--update"))
					forceUpdate = true;

			if (forceUpdate) {
				registry.rebind("GradeCompilerService", stub);
			} else {
				registry.bind("GradeCompilerService", stub);
			}

			System.err.println("GradeCompilerService running");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

}