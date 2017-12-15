package com.ryanxli.sqlite_example;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable{


    // private variables
    public int _id;
    public String _name;
    public String _phone_number;
    public String _email;

    public Contact() {
    }

    // constructor
    public Contact(int id, String name, String _phone_number, String _email) {
	this._id = id;
	this._name = name;
	this._phone_number = _phone_number;
	this._email = _email;

    }

    // constructor
    public Contact(String name, String _phone_number, String _email) {
	this._name = name;
	this._phone_number = _phone_number;
	this._email = _email;
    }

    // getting ID
    public int getID() {
	return this._id;
    }

    // setting id
    public void setID(int id) {
	this._id = id;
    }

    // getting name
    public String getName() {
	return this._name;
    }

    // setting name
    public void setName(String name) {
	this._name = name;
    }

    // getting phone number
    public String getPhoneNumber() {
	return this._phone_number;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number) {
	this._phone_number = phone_number;
    }

    // getting email
    public String getEmail() {
	return this._email;
    }

    // setting email
    public void setEmail(String email) {
	this._email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_name);
        dest.writeString(_phone_number);
        dest.writeString(_email);
    }
    
    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }
        
        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public Contact(Parcel parcel) {
        _id = parcel.readInt();
        _name = parcel.readString();
        _phone_number = parcel.readString();
        _email = parcel.readString();
    }

    public String toString() {
        return Integer.toString(_id) + ","
                + _name + "," + _phone_number + "," + _email;
    }

    public static Contact fromString(String str) {
        String[] fields = str.split(",");
        Contact ret = new Contact();
        ret.setID(Integer.parseInt(fields[0]));
        ret.setName(fields[1]);
        ret.setPhoneNumber(fields[2]);
        ret.setEmail(fields[3]);
        return ret;
    }
}