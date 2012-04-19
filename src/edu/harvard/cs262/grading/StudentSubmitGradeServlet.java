package edu.harvard.cs262.grading;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StudentSubmitGradeServlet extends HttpServlet {

    GradeStorageService gradeStorage;
    SubmissionStorageService submissionStorage;
    
    public void lookupServices() {

        try {
            // get reference to database service
        	Registry registry = LocateRegistry.getRegistry();
        	gradeStorage = (GradeStorageService) registry.lookup("GradeStorageService");
        } catch (RemoteException e) {
            System.err.println("AdminGetGradesServlet: Could not contact registry.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NotBoundException e) {
            System.err.println("AdminGetGradesServlet: Could not find GradeStorageService in registry.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if(gradeStorage == null) {
        	System.err.println("Could not fine GradeStorageService");
        	System.exit(-1);
        }

        try {
            // get reference to database service
        	Registry registry = LocateRegistry.getRegistry();
        	submissionStorage = (SubmissionStorageService) registry.lookup("SubmissionStorageService");
        } catch (RemoteException e) {
            System.err.println("AdminGetSubmissionsServlet: Could not contact registry.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NotBoundException e) {
            System.err.println("AdminGetSubmissionsServlet: Could not find SubmissionStorageService in registry.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if(submissionStorage == null) {
        	System.err.println("Could not fine SubmissionStorageService");
        	System.exit(-1);
        }
    	
    }

	public void init(ServletConfig config) throws ServletException {

		super.init(config);
	        
		lookupServices();

	}
	
	// passed id for student and actual grade
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	// get posted parameters (may have to update parameter names)
    	String rawScore = request.getParameter("score");
    	String rawGrader = request.getParameter("uid");
    	String rawStudent = request.getParameter("student");
    	String rawAssignment = request.getParameter("assignment");
    	
    	if(rawScore == null || rawGrader == null || rawStudent == null || rawAssignment == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "parameters not set");
    	} else {
    		
    		Grade grade;

	    	// try to convert parameters into usable format
	    	try{
		    	Integer studentID = Integer.parseInt(rawStudent);
		    	Integer graderID = Integer.parseInt(rawGrader);
		    	Integer scoreValue = Integer.parseInt(rawScore);
		    	Integer assignmentID = Integer.parseInt(rawAssignment);
		    	Score score = new ScoreImpl(scoreValue,scoreValue);
    	    	Student grader = new StudentImpl(graderID);
    	    	Student student = new StudentImpl(studentID);
    	    	Assignment assignment = new AssignmentImpl(assignmentID);
		    	
		    	grade = new GradeImpl(score,grader);
		    	Submission submission = submissionStorage.getLatestSubmission(student, assignment);
	    		gradeStorage.submitGrade(submission, grade);

	        	response.setContentType("text/Javascript");
	        	response.setCharacterEncoding("UTF-8");
	        	response.getWriter().write("Succesfully submitted grade for student "+studentID+"'s assignment "+assignmentID+".");
		    	
	    	} catch (NumberFormatException e){
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
	                    "invalid values given");
	    	} catch (NullPointerException e) {
	    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	    				"grade upload failed");
	    		e.printStackTrace();
	    	}	
    	}
    }

}
