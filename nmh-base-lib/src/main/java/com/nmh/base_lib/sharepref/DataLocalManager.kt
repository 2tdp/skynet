package com.nmh.base_lib.sharepref

import android.content.Context
import com.google.gson.Gson
import com.nmh.base_lib.ui.language.LanguageModel
import org.json.JSONException
import org.json.JSONObject

class DataLocalManager {

    private var mySharedPreferences: MySharePreferences? = null

    companion object {
        private var instance: DataLocalManager? = null
        fun init(context: Context) {
            instance = DataLocalManager()
            instance?.mySharedPreferences = MySharePreferences(context)
        }

        private fun getInstance(): DataLocalManager {
            if (instance == null) instance = DataLocalManager()
            return instance!!
        }

        fun setFirstInstall(key: String?, isFirst: Boolean) {
            getInstance().mySharedPreferences!!.putBooleanValue(key, isFirst)
        }

        fun getFirstInstall(key: String?): Boolean {
            return getInstance().mySharedPreferences!!.getBooleanValue(key, true)
        }

        fun setBoolean(key: String, isFirst: Boolean) {
            getInstance().mySharedPreferences!!.putBooleanValue(key, isFirst)
        }

        fun getBoolean(key: String, default: Boolean): Boolean {
            return getInstance().mySharedPreferences!!.getBooleanValue(key, default)
        }

        fun setOption(option: String?, key: String?) {
            getInstance().mySharedPreferences!!.putStringWithKey(key, option)
        }

        fun getOption(key: String?): String? {
            return getInstance().mySharedPreferences!!.getStringWithKey(key, "")
        }

        fun setInt(count: Int, key: String?) {
            getInstance().mySharedPreferences!!.putIntWithKey(key, count)
        }

        fun getInt(key: String?): Int {
            return getInstance().mySharedPreferences!!.getIntWithKey(key, -1)
        }

        fun setLong(count: Long, key: String?) {
            getInstance().mySharedPreferences!!.putLongWithKey(key, count)
        }

        fun getLong(key: String?): Long {
            return getInstance().mySharedPreferences!!.getLongWithKey(key, -1L)
        }

        fun setLanguage(key: String, lang: LanguageModel) {
            getInstance().mySharedPreferences?.putStringWithKey(key, Gson().toJsonTree(lang).asJsonObject.toString())
        }

        fun getLanguage(key: String): LanguageModel? {
            val strJson = getInstance().mySharedPreferences!!.getStringWithKey(key, "")!!
            var lang: LanguageModel? = null
            try {
                lang = Gson().fromJson(JSONObject(strJson).toString(), LanguageModel::class.java)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lang
        }
    }
}