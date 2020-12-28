package org.apache.bookkeeper.bookie.storage.ldb.entity;

public class WriteTestEntity {
    private int entryId;
    private int ledgerId;
    private boolean validEntry;
    private long cacheSize;
    private int entrySize;
    private int segmentSize;
    private boolean doublePut;

    public WriteTestEntity(int ledgerId, int entryId) {
        this.ledgerId = ledgerId;
        this.entryId = entryId;
    }
    
    public WriteTestEntity(int ledgerId, int entryId, boolean validEntry, long cacheSize, int entrySize, int segmentSize, boolean doublePut) {
    	//put case
    	this.cacheSize = cacheSize;
        this.entryId = entryId;
        this.ledgerId = ledgerId;
        this.validEntry = validEntry;
        this.entrySize = entrySize;
        this.segmentSize = segmentSize;
        this.doublePut = doublePut;
    }   


    public int getEntryId() {
        return entryId;
    }

    public int getLedgerId() {
        return ledgerId;
    }

    public boolean isValidEntry() {
        return validEntry;
    }

	public long getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}

	public int getEntrySize() {
		return entrySize;
	}

	public void setEntrySize(int entrySize) {
		this.entrySize = entrySize;
	}

	public int getSegmentSize() {
		return segmentSize;
	}

	public void setSegmentSize(int segmentSize) {
		this.segmentSize = segmentSize;
	}

	public boolean isDoublePut() {
		return doublePut;
	}

	public void setDoublePut(boolean doublePut) {
		this.doublePut = doublePut;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + entryId;
		result = prime * result + ledgerId;
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
		WriteTestEntity other = (WriteTestEntity) obj;
		if (entryId != other.entryId)
			return false;
		if (ledgerId != other.ledgerId)
			return false;
		return true;
	}
}
