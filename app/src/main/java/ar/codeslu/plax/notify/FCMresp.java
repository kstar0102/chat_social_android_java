package ar.codeslu.plax.notify;

import java.util.List;


/**
 * Created by CodeSlu on 9/4/2018.
 */

public class FCMresp {
    public long multicastID;
    public int success,failure,canonicalID;
    public List<Result> resultList;

    public FCMresp(long multicastID, int success, int failure, int canonicalID, List<Result> resultList) {
        this.multicastID = multicastID;
        this.success = success;
        this.failure = failure;


        this.canonicalID = canonicalID;
        this.resultList = resultList;
    }

    public FCMresp() {
    }

    public long getMulticastID() {
        return multicastID;
    }

    public void setMulticastID(long multicastID) {
        this.multicastID = multicastID;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonicalID() {
        return canonicalID;
    }

    public void setCanonicalID(int canonicalID) {
        this.canonicalID = canonicalID;
    }

    public List<Result> getResultList() {
        return resultList;
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
    }
}
