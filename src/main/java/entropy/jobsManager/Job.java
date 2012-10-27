package entropy.jobsManager;
/*
 * Copyright (c) 2010 Fabien Hermenier
 *
 *      This file is part of Entropy.
 *
 *      Entropy is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      Entropy is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with Entropy.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A job is the primary component exchanged by JobDispatcher and JobHandler.
 * It has an unique identifier and is composed by several pair of key/value.
 *
 * @author Fabien Hermenier
 */
public class Job {

    /**
     * The identifier of the job.
     */
    private int id;

    /**
     * The key/value pairs of the job.
     */
    private Map<String, String> values;

    /**
     * The moment the job was enqueued.
     */
    private long enqueuedTime = -1L;

    /**
     * The moment the job was dequeued.
     */
    private long dequeuedTime = -1L;

    /**
     * The moment the job was committed.
     */
    private long commitedTime = -1L;

    /**
     * Make a new job using a specific ID. Must be unique!
     *
     * @param id the identifier of the job
     */
    public Job(int id) {
        this.id = id;
        this.values = new HashMap<String, String>();
    }

    /**
     * Get all the keys composed the job.
     *
     * @return a set of keys, may be empty
     */
    public Set<String> getKeys() {
        return values.keySet();
    }

    /**
     * Get the value associated to a key.
     *
     * @param k the key
     * @return the associated value or null
     */
    public String get(String k) {
        return values.get(k);
    }

    /**
     * Associate a value to a key.
     * If the key already has a value, it is overwrittent.
     *
     * @param k the key
     * @param v the value
     */
    public void put(String k, String v) {
        values.put(k, v);
    }

    /**
     * Get the identifier of the job.
     *
     * @return the identifier provided at instantiation
     */
    public int getId() {
        return id;
    }


    public String toJSON() {
        return new Gson().toJson(this);
    }

    public static Job fromJSON(String buffer) {
        return new Gson().fromJson(buffer, Job.class);
    }

    /**
     * Textual representation of the job.
     *
     * @return a formatted string
     */
    public String toString() {
        return "job(" + id + ", " + values + ")";
    }

    /**
     * Ge the hashCode for the job.
     *
     * @return {@code #getId()}
     */
    @Override
    public int hashCode() {
        return id;
    }

    /**
     * Check wether two jobs are equals or not.
     *
     * @param obj the object to test
     * @return true if {@code obj} is an instance of Job and has the same identifier
     */
    @Override
    public boolean equals(Object obj) {
        return (this.getClass() == obj.getClass() && id == ((Job) obj).id);
    }

    /**
     * Get the moment the job was dequeued.
     *
     * @return a time
     */
    public long getDequeuedTime() {
        return dequeuedTime;
    }

    /**
     * Set the moment the job was dequeued.
     *
     * @param dequeuedTime a time
     */
    void setDequeuedTime(long dequeuedTime) {
        this.dequeuedTime = dequeuedTime;
    }

    /**
     * Get the moment the job was enqueued.
     *
     * @return a time
     */
    public long getEnqueuedTime() {
        return enqueuedTime;
    }

    /**
     * Set the moment the job was enqueued.
     *
     * @param t a time
     */
    void setEnqueuedTime(long t) {
        this.enqueuedTime = t;
    }

    /**
     * Get the moment the job was commited.
     *
     * @return a time
     */
    public long getCommitedTime() {
        return commitedTime;
    }

    /**
     * Set the moment the job was commited.
     *
     * @param commitedTime a time
     */
    void setCommitedTime(long commitedTime) {
        this.commitedTime = commitedTime;
    }
}
