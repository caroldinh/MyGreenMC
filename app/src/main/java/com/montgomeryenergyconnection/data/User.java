package com.montgomeryenergyconnection.data;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class User {

    public String uid;
    public String name;
    public int points = 0;

    private ArrayList<Task> complete = new ArrayList<>();
    private ArrayList<Task> inProgress = new ArrayList<>();
    private Context context;

    public User(String mUID, String mName, Context mContext){
        this.uid = mUID;
        this.name = mName;
        this.context = mContext;
        readTasks();
        saveUserFile();
    }

    public User(String mUID, String mName, Context mContext, DatabaseReference mDatabase){
        this.uid = mUID;
        this.name = mName;
        this.context = mContext;

        DatabaseReference ref = mDatabase.child("users").child(uid);

        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        points = Integer.parseInt(dataSnapshot.child("points").getValue().toString());

                        for(DataSnapshot child :  dataSnapshot.child("inProgress").getChildren()){
                            String name = child.child("name").getValue().toString();
                            String description = child.child("description").getValue().toString();
                            String category = child.child("category").getValue().toString();
                            int pointVal = Integer.parseInt(child.child("pointVal").getValue().toString());
                            int streak = Integer.parseInt(child.child("streak").getValue().toString());
                            if(child.child("checkVal").getValue().toString().equals("true") || child.child("checkVal").getValue().toString().equals("false")){
                                boolean checkVal = Boolean.parseBoolean(child.child("checkVal").getValue().toString());
                                boolean input = Boolean.parseBoolean(child.child("input").getValue().toString());
                                BooleanTask task = new BooleanTask(name, description, pointVal, streak, category, checkVal);
                                task.setInput(input);
                                inProgress.add(task);
                            } else{
                                int checkVal = Integer.parseInt(child.child("checkVal").getValue().toString());
                                int input = Integer.parseInt(child.child("input").getValue().toString());
                                String comparison = child.child("comparison").getValue().toString();
                                IntTask task = new IntTask(name, description, pointVal, streak, category, checkVal, comparison);
                                task.setInput(input);
                                inProgress.add(task);
                            }
                        }
                        for(DataSnapshot child :  dataSnapshot.child("complete").getChildren()){
                            String name = child.child("name").getValue().toString();
                            String description = child.child("description").getValue().toString();
                            String category = child.child("category").getValue().toString();
                            int pointVal = Integer.parseInt(child.child("pointVal").getValue().toString());
                            int streak = Integer.parseInt(child.child("streak").getValue().toString());
                            if(child.child("checkVal").getValue().toString().equals("true") || child.child("checkVal").getValue().toString().equals("false")){
                                boolean checkVal = Boolean.parseBoolean(child.child("checkVal").getValue().toString());
                                boolean input = Boolean.parseBoolean(child.child("input").getValue().toString());
                                BooleanTask task = new BooleanTask(name, description, pointVal, streak, category, checkVal);
                                task.setInput(input);
                                complete.add(task);
                            } else{
                                int checkVal = Integer.parseInt(child.child("checkVal").getValue().toString());
                                int input = Integer.parseInt(child.child("input").getValue().toString());
                                String comparison = child.child("comparison").getValue().toString();
                                IntTask task = new IntTask(name, description, pointVal, streak, category, checkVal, comparison);
                                task.setInput(input);
                                complete.add(task);
                            }
                        }

                        saveUserFile();
                        Intent intent = new Intent("custom-message");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });

    }

    public void saveToCloud(DatabaseReference mDatabase){
        mDatabase.child("users").child(uid).setValue(this);
    }


    public void readTasks(){

        String dir = context.getFilesDir().getAbsolutePath();
        File f = new File(dir + "/tasks.txt");
        String line;

        try{

            f.createNewFile(); // if file already exists will do nothing

            BufferedReader br = new BufferedReader(new FileReader(f));

            ArrayList<String> lines = new ArrayList<String>();

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();

            for(String l : lines){

                Log.d("LINE: ", l);

                Task t;

                ArrayList<String> args = new ArrayList<>();
                String current = "";
                for(int i = 0; i < l.length(); i++){
                    if(l.charAt(i) == '&' || l.charAt(i) == '?') {
                        args.add(current);
                        current = "";
                    } else{
                        current += l.charAt(i);
                    }
                }
                args.add(current);

                Log.d("STRING SIZE", args.size() + "");
                for(String s: args){
                    Log.d("STRING", s);
                }

                if(args.get(args.size() - 1).equals("true") || args.get(args.size() - 1).equals("false")){
                    t = new BooleanTask(args.get(0), args.get(1), Integer.parseInt(args.get(2)),
                            Integer.parseInt(args.get(3)), args.get(4), Boolean.parseBoolean(args.get(5)));
                    ((BooleanTask) t).setInput(Boolean.parseBoolean(args.get(args.size() - 1)));
                } else{
                    t = new IntTask(args.get(0), args.get(1), Integer.parseInt(args.get(2)),
                            Integer.parseInt(args.get(3)), args.get(4), Integer.parseInt(args.get(5)), args.get(6));
                    ((IntTask) t).setInput(Integer.parseInt(args.get(args.size() - 1)));
                }
                if(!t.verify()){
                    inProgress.add(t);
                } else{
                    complete.add(t);
                }

            }

        } catch(Exception e){
            Log.d("EXCEPTION: ", e.getStackTrace().toString());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        countPoints();

    }

    public void countPoints(){

        int points = 0;

        for (Task task : inProgress){
            points += task.getStreak() * task.getPointVal();
        }

        for (Task task : complete){
            points += task.getStreak() * task.getPointVal();
        }

        this.points = points;

    }

    public void addTask(Task task){

        if(task instanceof IntTask){
            int val = ((IntTask) task).getCheckVal();
            String comparison = ((IntTask) task).getComparison();

            // Write "default" value
            if(comparison.equals(">=")){
                val = 0;
            } else{
                val++;
            }
            ((IntTask) task).setInput(val);

        // Else if task is a BooleanTask:
        } else {
            boolean val = ((BooleanTask) task).getCheckVal();
            ((BooleanTask) task).setInput(!val);
        }

        inProgress.add(task);

    }

    public void resetTasks(){

        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(complete);
        allTasks.addAll(inProgress);
        clearTasks();
        for(Task task : allTasks){
            addTask(task);
        }
        saveTasks();

        Log.d("RESET", inProgress.toString());
        Log.d("RESET", complete.toString());
        Log.d("RESET", inProgress.size() + "");
        Log.d("RESET", complete.size()+"");

    }

    public void clearTasks(){
        complete.clear();
        inProgress.clear();
    }

    public void clearFile(){

        String dir = context.getFilesDir().getAbsolutePath();
        File f = new File(dir + "/tasks.txt");

        try{
            f.createNewFile(); // if file already exists will do nothing

            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/tasks.txt", false));

            writer.write("");
            writer.close();

        } catch(Exception e){
            Log.d("EXCEPTION: ", e.getStackTrace().toString());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }



    public void saveTasks(){

        clearFile();

        String dir = context.getFilesDir().getAbsolutePath();
        File f = new File(dir + "/tasks.txt");

        try{
            f.createNewFile(); // if file already exists will do nothing
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/tasks.txt", true));

            ArrayList<Task> all = new ArrayList<>();
            all.addAll(inProgress);
            all.addAll(complete);

            for(Task task : all){
                writer.write(task.toString() + "\n");
            }
            writer.close();

        } catch(Exception e){
            Log.d("EXCEPTION: ", e.getStackTrace().toString());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void saveUserFile(){
        String dir = context.getFilesDir().getAbsolutePath();
        File f = new File(dir + "/userData.txt");

        try{
            f.createNewFile(); // if file already exists will do nothing

            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/userData.txt", false));
            writer.write(uid + "\n");
            writer.write(name + "\n");
            writer.write(points + "\n");
            writer.close();

        } catch(Exception e){
            Log.d("EXCEPTION: ", e.getStackTrace().toString());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void readUserFile(){

        String dir = context.getFilesDir().getAbsolutePath();
        File f = new File(dir + "/userData.txt");
        String line;

        try{

            f.createNewFile(); // if file already exists will do nothing

            BufferedReader br = new BufferedReader(new FileReader(f));

            ArrayList<String> lines = new ArrayList<String>();

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();

            this.uid = lines.get(0);
            this.name = lines.get(1);
            this.points = Integer.parseInt(lines.get(2));

        } catch(Exception e){
            Log.d("EXCEPTION: ", e.getStackTrace().toString());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    public ArrayList<Task> getInProgress(){ return inProgress; }
    public ArrayList<Task> getComplete() { return complete; }
    public int getPoints(){ return points; }


}
