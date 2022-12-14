package cc.fastcv.sqlite.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cc.fastcv.sqlite.R
import cc.fastcv.sqlite.db.Database
import cc.fastcv.sqlite.entity.StudentInfo
import cc.fastcv.sqlite.util.*

class StudentInfoEditActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "StudentInfoEditActivity"

        fun intoActivity(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, StudentInfoEditActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_info_edit)

        initListener()
    }

    private fun initListener() {
        findViewById<TextView>(R.id.tv_random).setOnClickListener {
            buildRandomStudentInfo()
        }

        findViewById<TextView>(R.id.tv_add).setOnClickListener {
            saveStudentInfo()
        }
    }

    private fun saveStudentInfo() {
        val name = getName()
        val phone = getPhone()
        val gender = getGender()
        val studentNumber = getStudentNumber()
        val birthDay = getBirthDay()
        val address = getAddress()
        val studentInfo = StudentInfo(0, studentNumber, name, gender, phone, birthDay, address)
        Database.getStudentInfoDao().insert(studentInfo)
    }

    private fun buildRandomStudentInfo() {
        val name = randomName()
        val phoneNumber = randomPhoneNumber()
        val genderIndex = randomGender()
        val registerYearIndex = randomYear()
        val birthYearIndex = randomYear()
        val birthMonthIndex = randomMonth()
        val birthDayIndex = randomDay()
        val address = randomAddress()

        setName(name)
        setPhone(phoneNumber)
        setGender(genderIndex)
        setRegisterYear(registerYearIndex)
        setBirthDay(birthYearIndex, birthMonthIndex, birthDayIndex)
        setAddress(address)
    }

    /**
     * ????????????
     */
    private fun getName(): String {
        return findViewById<EditText>(R.id.et_name).text.toString().trim()
    }

    /**
     * ????????????
     */
    private fun setName(name: String) {
        findViewById<EditText>(R.id.et_name).setText(name)
    }

    /**
     * ???????????????
     */
    private fun getPhone(): String {
        return findViewById<EditText>(R.id.et_phone).text.toString().trim()
    }

    /**
     * ???????????????
     */
    private fun setPhone(phone: String) {
        findViewById<EditText>(R.id.et_phone).setText(phone)
    }

    /**
     * ????????????
     * 0  ---   ???
     * 1  ---   ???
     */
    private fun getGender(): Int {
        return findViewById<Spinner>(R.id.spinner_gender).selectedItemPosition
    }

    /**
     * ????????????
     * 0  ---   ???
     * 1  ---   ???
     */
    private fun setGender(genderIndex: Int) {
        return findViewById<Spinner>(R.id.spinner_gender).setSelection(genderIndex)
    }

    /**
     * ????????????
     */
    private fun getStudentNumber(): String {
        val registerYear =
            resources.getStringArray(R.array.year)[findViewById<Spinner>(R.id.spinner_register_year).selectedItemPosition]
        return registerYear + String.format("%02d", getGender()) + System.currentTimeMillis()
    }

    /**
     * ??????????????????
     */
    private fun setRegisterYear(registerYearIndex: Int) {
        findViewById<Spinner>(R.id.spinner_register_year).setSelection(registerYearIndex)
    }

    /**
     * ??????????????????
     */
    private fun getBirthDay(): String {
        //????????????
        val year =
            resources.getStringArray(R.array.year)[findViewById<Spinner>(R.id.spinner_year).selectedItemPosition]
        //????????????
        val month = findViewById<Spinner>(R.id.spinner_month).selectedItemPosition + 1
        //?????????
        val day = findViewById<Spinner>(R.id.spinner_day).selectedItemPosition + 1
        return "$year-${String.format("%02d", month)}-${String.format("%02d", day)}"
    }

    /**
     * ??????????????????
     */
    private fun setBirthDay(yearIndex: Int, monthIndex: Int, dayIndex: Int) {
        //????????????
        findViewById<Spinner>(R.id.spinner_year).setSelection(yearIndex)
        //????????????
        findViewById<Spinner>(R.id.spinner_month).setSelection(monthIndex)
        //?????????
        findViewById<Spinner>(R.id.spinner_day).setSelection(dayIndex)
    }

    /**
     * ??????????????????
     */
    private fun getAddress(): String {
        return findViewById<EditText>(R.id.et_address).text.toString().trim()
    }

    /**
     * ??????????????????
     */
    private fun setAddress(address: String) {
        findViewById<EditText>(R.id.et_address).setText(address)
    }
}