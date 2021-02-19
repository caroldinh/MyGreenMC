package com.mygreenmc.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class User {

    public String uid;
    public String name;
    private ArrayList<Task> complete = new ArrayList<>();
    private ArrayList<Task> inProgress = new ArrayList<>();
    private Context context;

    public User(String mUID, String mName, Context mContext){
        this.uid = mUID;
        this.name = mName;
        this.context = mContext;
        saveUserFile();

        readTasks();

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

    public void updateTask(String name, int val){

        IntTask t;
        boolean found = false;
        for(Task task : inProgress){
            if(task.getName().equals(name)){
                t = (IntTask)task;
                t.setInput(val);
                if(task.verify()){
                    inProgress.remove(task);
                    complete.add(task);
                }
            }
        }
        if(!found){
            for(Task task : complete) {
                if (task.getName().equals(name)) {
                    t = (IntTask) task;
                    t.setInput(val);
                    if (!task.verify()) {
                        complete.remove(task);
                        inProgress.add(task);
                    }
                }
            }
        }

    }

    public void updateTask(String name, boolean val){

        BooleanTask t;
        boolean found = false;
        for(Task task : inProgress){
            if(task.getName().equals(name)){
                t = (BooleanTask) task;
                t.setInput(val);
                if(task.verify()){
                    inProgress.remove(task);
                    complete.add(task);
                }
            }
        }
        if(!found){
            for(Task task : complete) {
                if (task.getName().equals(name)) {
                    t = (BooleanTask) task;
                    t.setInput(val);
                    if (!task.verify()) {
                        complete.remove(task);
                        inProgress.add(task);
                    }
                }
            }
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
            writer.close();

        } catch(Exception e){
            Log.d("EXCEPTION: ", e.getStackTrace().toString());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public ArrayList<Task> getInProgress(){ return inProgress; }
    public ArrayList<Task> getComplete() { return complete; }


}
