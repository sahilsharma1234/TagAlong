package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelViewAllRidesDriverResponse {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ModelViewAllRidesDriverResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static class TimelineData {

        @SerializedName("_id")
        public String _id;
        @SerializedName("type")
        public int type;
        @SerializedName("postUrl")
        public String postUrl;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getPostUrl() {
            return postUrl;
        }

        public void setPostUrl(String postUrl) {
            this.postUrl = postUrl;
        }
    }

    public static class Data {

        @SerializedName("heading")
        public String heading;
        @SerializedName("timelineData")
        public List<TimelineData> timelineData;

        public String getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading = heading;
        }

        public List<TimelineData> getTimelineData() {
            return timelineData;
        }

        public void setTimelineData(List<TimelineData> timelineData) {
            this.timelineData = timelineData;
        }
    }
}
