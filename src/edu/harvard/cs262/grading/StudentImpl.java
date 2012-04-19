package edu.harvard.cs262.grading;

public class StudentImpl implements Student {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3416718070943451888L;
	private long studentID;
	
	public StudentImpl() {
		this.studentID = 0;
	}
	
	public StudentImpl(long studentID) {
		this.studentID = studentID;
	}

	public long studentID() {
		return this.studentID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (studentID ^ (studentID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentImpl other = (StudentImpl) obj;
		if (studentID != other.studentID)
			return false;
		return true;
	}
}
