package edu.harvard.cs262.grading.clients.web;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.harvard.cs262.grading.service.Assignment;
import edu.harvard.cs262.grading.service.AssignmentImpl;
import edu.harvard.cs262.grading.service.ServiceLookupUtility;
import edu.harvard.cs262.grading.service.Student;
import edu.harvard.cs262.grading.service.StudentImpl;
import edu.harvard.cs262.grading.service.Submission;
import edu.harvard.cs262.grading.service.SubmissionImpl;
import edu.harvard.cs262.grading.service.SubmissionStorageService;
import edu.harvard.cs262.grading.service.web.ServletConfigReader;

public class StudentSubmitSubmissionServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7755775296767098218L;
	private SubmissionStorageService submissionStorage;
	
	public void lookupServices() {
		
		try {
			submissionStorage = (SubmissionStorageService) ServiceLookupUtility.lookupService(new ServletConfigReader(this.getServletContext()), "SubmissionStorageService");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(submissionStorage == null) {
			System.err.println("StudentSubmissionServlet: Looking up SubmissionStorageService failed.");
		}
	    	
	}

	public void init(ServletConfig config) throws ServletException {

		super.init(config);
	        
		lookupServices();

	}
	
	// passed id for student and actual submission
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	// get posted parameters (may have to update parameter names)
    	String rawStudent = request.getParameter("uid");
    	String rawAssignment = request.getParameter("assignment");
    	String rawSubmission = request.getParameter("submission");
    	
    	if(rawStudent == null || rawSubmission == null || rawAssignment == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "parameters not set");
    	} else {
    		
    		Submission submission;

	    	// try to convert parameters into usable format
	    	try{
		    	Long studentID = Long.parseLong(rawStudent);
		    	Long assignmentID = Long.parseLong(rawAssignment);
		    	Assignment assignment = new AssignmentImpl(assignmentID);
    	    	Student student = new StudentImpl(studentID);
		    	
		    	submission = new SubmissionImpl(student, assignment, rawSubmission.getBytes());
	    		submissionStorage.storeSubmission(submission);

	        	response.setContentType("text/Javascript");
	        	response.setCharacterEncoding("UTF-8");
	        	response.getWriter().write("Succesfully submitted submission for assignment "+assignmentID+".");
		    	
	    	} catch (NumberFormatException e){
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
	                    "invalid values given");
	    	} catch (NullPointerException e) {
	    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	    				"submission upload failed");
	    		e.printStackTrace();
	    	}	
    	}
    }
}

