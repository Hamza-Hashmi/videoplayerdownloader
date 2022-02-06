package com.example.videoplayer_and_downloader.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceData(private  var context: Context){


        @SuppressLint("WrongConstant")
        fun putBoolean(key: String, value: Boolean) {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            val editor = sharedPref.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        @SuppressLint("WrongConstant")
        fun putInt(key: String, value: Int) {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            val editor = sharedPref.edit()
            editor.putInt(key, value)
            editor.apply()
        }
@SuppressLint("WrongConstant")
fun putString(key: String, value: String) {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            val editor = sharedPref.edit()
            editor.putString(key, value)
            editor.apply()
        }

        @SuppressLint("WrongConstant")
        fun putFloat(key: String, value: Float) {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            val editor = sharedPref.edit()
            editor.putFloat(key, value)
            editor.apply()
        }

        @SuppressLint("WrongConstant")
        fun putSet(key: String, value: Set<String>) {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            val editor = sharedPref.edit()
            editor.putStringSet(key, value)
            editor.apply()
        }


        @SuppressLint("WrongConstant")
        fun getFloat(key: String, default: Float = 0f): Float {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            return sharedPref.getFloat(key, default)
        }

        @SuppressLint("WrongConstant")
        fun getBoolean(key: String, default: Boolean = false): Boolean {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            return sharedPref.getBoolean(key, default)
        }

        @SuppressLint("WrongConstant")
        fun getInt(key: String, default: Int = 0): Int {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            return sharedPref.getInt(key, default)
        }
    @SuppressLint("WrongConstant")
    fun getString(key: String, default: String ): String {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            return sharedPref.getString(key, default)?:default
        }

        @SuppressLint("WrongConstant")
        fun getSetString(

            key: String,
            default: Set<String> = HashSet()
        ): Set<String> {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.preferenceName, Constants.preferenceMode)
            return sharedPref.getStringSet(key, default) ?: default
        }







}