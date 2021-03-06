package edu.harvard.cs262.grading.server.services;

import java.rmi.RemoteException;
import java.util.Set;

/**
 * Service for persistent storage of assignments.
 */
public interface AssignmentStorageService extends Service {

	/**
	 * Add a new assignment
	 * 
	 * @param ID
	 *            a unique identifier for the assignment
	 * @param desc
	 *            a string describing the assignment
	 * @throws RemoteException
	 */
	public void addNewAssignment(long ID, String desc) throws RemoteException;

	/**
	 * Retrieve the existing assignments
	 * 
	 * @return a set of assignments
	 * @throws RemoteException
	 */
	public Set<Assignment> getAssignments() throws RemoteException;
}
