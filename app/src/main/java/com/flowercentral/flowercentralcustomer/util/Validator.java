package com.flowercentral.flowercentralcustomer.util;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ashish on 10/15/16.
 */

public class Validator {
    private static final String NUMBER_PATTERN = "";
    private static final String CHARACTER_PATTERN = "^[a-zA-Z]+$";
    private static final String CHARACTER_SPACE_PATTERN = "^[a-zA-Z ]+$";
    private static final String CHARACTER_WITH_NUMBER_SPACE_PATTERN = "^[a-zA-Z0-9 ]+$";
    private static final String PHONE_NUMBER_PATTERN = "";

    public static boolean validateEmail(String _email){

        boolean status = false;

        if(_email != null && _email.trim().length() > 0){

            Pattern pattern = Patterns.EMAIL_ADDRESS;
            Matcher matcher = pattern.matcher(_email);
            status = matcher.matches();
        }else{
            status = false;
        }
        return status;
    }

    public static boolean validateNumber(String _number, String _pattern){

        boolean status = false;

        if(_pattern == null){
            _pattern = NUMBER_PATTERN;
        }

        if((_number != null && _number.trim().length() > 0) && (_pattern != null && _pattern.trim().length() > 0)){

            Pattern pattern = Pattern.compile(_pattern);
            Matcher matcher = pattern.matcher(_number);
            status = matcher.matches();
        }else{
            status = false;
        }
        return status;
    }

    public static boolean validateCharacterOnly(String _text, String _pattern){

        boolean status = false;

        if(_pattern == null){
            _pattern = CHARACTER_PATTERN;
        }

        if((_text != null && _text.trim().length() > 0 )  && (_pattern != null && _pattern.trim().length() > 0)){

            Pattern pattern = Pattern.compile(_pattern);
            Matcher matcher = pattern.matcher(_text);
            status = matcher.matches();
        }else{
            status = false;
        }
        return status;
    }

    public static boolean validateCharacterWithSpaceOnly(String _text, String _pattern){

        boolean status = false;

        if(_pattern == null){
            _pattern = CHARACTER_SPACE_PATTERN;
        }

        if((_text != null && _text.trim().length() > 0 )  && (_pattern != null && _pattern.trim().length() > 0)){

            Pattern pattern = Pattern.compile(_pattern);
            Matcher matcher = pattern.matcher(_text);
            status = matcher.matches();
        }else{
            status = false;
        }
        return status;
    }

    public static boolean validateCharacterWithNumberAndSpaceOnly(String _text, String _pattern){

        boolean status = false;

        if(_pattern == null){
            _pattern = CHARACTER_WITH_NUMBER_SPACE_PATTERN;
        }

        if((_text != null && _text.trim().length() > 0 )  && (_pattern != null && _pattern.trim().length() > 0)){

            Pattern pattern = Pattern.compile(_pattern);
            Matcher matcher = pattern.matcher(_text);
            status = matcher.matches();
        }else{
            status = false;
        }
        return status;
    }


    public static boolean validatePhoneNumber(String _phoneNumber){

        boolean status = false;

        if(_phoneNumber != null && _phoneNumber.trim().length() > 0){

            Pattern pattern = Patterns.PHONE;
            Matcher matcher = pattern.matcher(_phoneNumber);
            status = matcher.matches();
        }else{
            status = false;
        }
        return status;
    }

    public static boolean checkFixedLength(String _text, int _length){
        boolean status = false;
        if(_text != null){
            int len = _text.trim().length();
            if(len == _length){
                status = true;
            }else{
                status = false;
            }

        }else{
            status = false;
        }
        return status;
    }

    public static boolean checkLengthInRange(String _text, int _minLength, int _maxLength){
        boolean status = false;
        if(_text != null){
            int len = _text.trim().length();
            if(len >= _minLength && len <=_maxLength){
                status = true;
            }else{
                status = false;
            }

        }else{
            status = false;
        }
        return status;
    }

    public static boolean checkMinLength(String _text, int _length){
        boolean status = false;
        if(_text != null){
            int len = _text.trim().length();
            if(len >= _length){
                status = true;
            }else{
                status = false;
            }

        }else{
            status = false;
        }
        return status;
    }

}
