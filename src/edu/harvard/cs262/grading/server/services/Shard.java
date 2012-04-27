package edu.harvard.cs262.grading.server.services;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Represents a sharding of the work, mapping graders to gradees.
 */
public interface Shard extends Serializable {

	/**
	 * @return the unique ID for this shard
	 */
	public int shardID();

	/**
	 * @return a mapping from grader to gradees
	 */
	public Map<Student, Set<Student>> getShard();

	/**
	 * Get the graders for a particular student
	 * 
	 * @param student
	 * @return the set of graders for a particular student
	 */
	public Set<Student> getGraders(Student student);

}