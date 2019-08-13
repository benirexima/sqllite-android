package com.example.benitoapp;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmList;

import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;


public class RealmActivity extends AppCompatActivity {


        Button btnAdd, btnRead, btnUpdate, btnDelete, btnDeleteWithSkill, btnFilterByAge;
        EditText inName, inAge, inSkill;
        TextView textView, txtFilterBySkill, txtFilterByAge;
        Realm mRealm;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.realm);

    btnAdd = findViewById(R.id.btnAdd);

    btnRead = findViewById(R.id.btnRead);

    btnUpdate = findViewById(R.id.btnUpdate);

    btnDelete = findViewById(R.id.btnDelete);

    btnDeleteWithSkill = findViewById(R.id.btnDeleteWithSkill);

    btnFilterByAge = findViewById(R.id.btnFilterByAge);

    textView = findViewById(R.id.textViewEmployees);
    txtFilterBySkill = findViewById(R.id.txtFilterBySkill);
    txtFilterByAge = findViewById(R.id.txtFilterByAge);

    inName = findViewById(R.id.inName);
    inAge = findViewById(R.id.inAge);
    inSkill = findViewById(R.id.inSkill);

    Realm.init(this);
    mRealm = Realm.getDefaultInstance();
}



public void onClick(View view) {

        switch (view.getId()) {

        case R.id.btnAdd:
        addEmployee();
        break;
        case R.id.btnRead:
        readEmployeeRecords();
        break;
        case R.id.btnUpdate:
        updateEmployeeRecords();
        break;
        case R.id.btnDelete:
        deleteEmployeeRecord();
        break;
        case R.id.btnDeleteWithSkill:
        deleteEmployeeWithSkill();
        break;
        case R.id.btnFilterByAge:
        filterByAge();
        break;
        }
        }

private void addEmployee(){

        Realm realm=null;
        try{
        realm=Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction(){

public void execute(Realm realm){


        try{


        if(!inName.getText().toString().trim().isEmpty()){
            Student student=new Student();
            student .name=inName.getText().toString().trim();

        if(!inAge.getText().toString().trim().isEmpty())
            student.age=Integer.parseInt(inAge.getText().toString().trim());


        String languageKnown=inSkill.getText().toString().trim();

        if(!languageKnown.isEmpty()){
        Skill skill=realm.where(Skill.class).equalTo(Skill.PROPERTY_SKILL,languageKnown).findFirst();

        if(skill==null){
        skill=realm.createObject(Skill.class,languageKnown);
        realm.copyToRealm(skill);
        }

            student.skills=new RealmList<>();
            student.skills.add(skill);
        }

        realm.copyToRealm(student);
        }

        }catch(RealmPrimaryKeyConstraintException e){
        Toast.makeText(getApplicationContext(),"Primary Key exists, Press Update instead",Toast.LENGTH_SHORT).show();
        }
        }
        });
        }finally{
        if(realm!=null){
        realm.close();
        }
        }
        }

    private void readEmployeeRecords() {


        mRealm.executeTransaction(new Realm.Transaction() {

            public void execute(Realm realm) {

                RealmResults<Student> results = realm.where(Student.class).findAll();
                textView.setText("");
                for (Student employee : results) {
                    textView.append(employee.name + " age: " + employee.age + " skill: " + employee.skills.size());
                }
            }
        });


    }

    private void updateEmployeeRecords() {

        mRealm.executeTransaction(new Realm.Transaction() {

            public void execute(Realm realm) {


                if (!inName.getText().toString().trim().isEmpty()) {


                    Student employee = realm.where(Student.class).equalTo(Student.PROPERTY_NAME, inName.getText().toString()).findFirst();
                    if (employee == null) {
                        employee = realm.createObject(Student.class, inName.getText().toString().trim());
                    }
                    if (!inAge.getText().toString().trim().isEmpty())
                        employee.age = Integer.parseInt(inAge.getText().toString().trim());

                    String languageKnown = inSkill.getText().toString().trim();
                    Skill skill = realm.where(Skill.class).equalTo(Skill.PROPERTY_SKILL, languageKnown).findFirst();

                    if (skill == null) {
                        skill = realm.createObject(Skill.class, languageKnown);
                        realm.copyToRealm(skill);
                    }


                    if (!employee.skills.contains(skill))
                        employee.skills.add(skill);

                }
            }
        });
    }

    private void deleteEmployeeRecord() {
        mRealm.executeTransaction(new Realm.Transaction() {

            public void execute(Realm realm) {
                Student employee = realm.where(Student.class).equalTo(Student.PROPERTY_NAME, inName.getText().toString()).findFirst();
                if (employee != null) {
                    employee.deleteFromRealm();
                }
            }
        });
    }

    private void deleteEmployeeWithSkill() {
        mRealm.executeTransaction(new Realm.Transaction() {

            public void execute(Realm realm) {

                RealmResults<Student> employees = realm.where(Student.class).equalTo("skills.skillName", inSkill.getText().toString().trim()).findAll();
                employees.deleteAllFromRealm();
            }
        });
    }


    private void filterByAge() {
        mRealm.executeTransaction(new Realm.Transaction() {

            public void execute(Realm realm) {

                RealmResults<Student> results = realm.where(Student.class).greaterThanOrEqualTo(Student.PROPERTY_AGE, 25).findAllSortedAsync(Student.PROPERTY_NAME);

                txtFilterByAge.setText("");
                for (Student employee : results) {
                    txtFilterByAge.append(employee.name + " age: " + employee.age + " skill: " + employee.skills.size());
                }
            }
        });
    }



    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            mRealm.close();
        }
    }
}