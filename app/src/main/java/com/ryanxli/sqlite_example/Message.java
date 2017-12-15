package com.ryanxli.sqlite_example;

/**
 * Created by Ryan on 2017/12/15.
 */

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Message {

    /**
     * Annotate a field that you want sent with the @JsonField marker.
     */

    public static final String DEFAULT = "DEFAULT";
    public static final String ADD = "ADD";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    @JsonField
    public String newContact;

    @JsonField
    public int contactID;

    @JsonField
    public String myAction;

    @JsonField
    public String sender;

    @JsonField
    public long sendTime;

    @JsonField
    public long receiveTime;

}
