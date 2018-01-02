package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {

    public static class Query{

        String func;
        String[] var;


        public Query(String b, String[] c){

            func = b;
            var = c;
            //num = n;
        }

//        public boolean equals(Object obj) {
//            Query s = (Query) obj;
////            boolean check = true;
////            for(int i = 0; i < var.length; i++){
////                if(!this.var[i].equals(s.var[i])){
////                    check = false;
////                }
////            }
//            return this.func.equals(s.func)  && this.var == s.var;
//        }
//
//        @Override
//        public int hashCode() {
//            final int prime = 31;//
//            //int result = prime * num + area;
//            int result = 0;
//            for(int i = 0; i < func.length(); i++){
//                result = result + prime * func.charAt(i);
//            }
//            return result + var.length * prime;
//        }

        public String toString(){
            StringBuilder s = new StringBuilder();
            s.append(func);
            for(int i = 0; i < var.length; i++){
                s.append(var[i]);
            }
            return s.toString();
        }
    }

//    public static class Sentence{
//
//        String func;
//        String[] var;
//        int index;
//
//        public Sentence(boolean a, String b, String[] c, int d){
//            func = b;
//            var = c;
//            index = d;
//        }
//    }

    public static void main(String[] args) {
        // write your code here
        File file = new File("input");

        int numQ = 0;
        int numS = 0;
        ArrayList<Query> query = new ArrayList<>();
        ArrayList<HashMap<String, String[]>> sentence = new ArrayList<>();

        try {

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                //method = sc.nextLine();
                if(sc.hasNext()){
                    numQ = sc.nextInt();
                }else{
                    break;
                }
                sc.nextLine();

                for(int i = 0; i < numQ; i++) {
                    String wholeLine = sc.nextLine();
                    query.add(getQuery(wholeLine));
                }
                if(sc.hasNext()){
                    numS = sc.nextInt();
                }else{
                    break;
                }
                sc.nextLine();

                for(int i = 0; i < numS; i++){
                    String wholeLine = sc.nextLine();
                    sentence.add(getSentence(wholeLine));
                }

                //sc.nextLine();

            }
            sc.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        boolean[] results = findResult(query, sentence);
//        for (int i = 0; i < results.length; i++){
//            System.out.println(results[i]);
//        }

        try{
            File fileout = new File("output.txt");
            PrintWriter writer = new PrintWriter(fileout, "UTF-8");
            for(int i = 0; i < results.length; i++){
                if(results[i]){
                    writer.println("TRUE");
                }else{
                    writer.println("FALSE");
                }
            }


            writer.close();
        } catch (IOException e) {
            // do something
        }

    }

    private static Query getQuery(String input){

        StringBuilder func = new StringBuilder(input);
        String[] var;

        int i = 0;
        while(func.charAt(i) != '('){
            i++;
        }

        func.delete(i, func.length());
        String rest = input.substring(i + 1, input.length() - 1).toString();
        var = rest.split("[,]");

        String function = func.toString();
        Query query = new Query(function, var);
        return query;
    }

    private static HashMap<String, String[]> getSentence(String input){
        HashMap<String, String[]> map = new HashMap<>();
        String[] literals = input.split("\\s+\\|\\s+");
        for(int i = 0; i < literals.length; i++){
            StringBuilder func = new StringBuilder(literals[i]);
            String[] var;

            int j = 0;
            while(func.charAt(j) != '('){
                j++;
            }

            func.delete(j, func.length());
            String rest = literals[i].substring(j + 1, literals[i].length() - 1).toString();
            var = rest.split("[,]");

            String function = func.toString();
            map.put(function, var);
        }
        return map;
    }

    private static boolean[] findResult(ArrayList<Query> query, ArrayList<HashMap<String, String[]>> sentence){

        boolean[] results = new boolean[query.size()];
        for (int i = 0; i < query.size(); i++) {

            ArrayList<HashMap<String, String[]>> currSentence = new ArrayList<>();
            String curr;
            String[] currVar = query.get(i).var;
            if(query.get(i).func.charAt(0) != '~'){
                curr = '~' + query.get(i).func;
                //String[] currVar = query.get(i).var;
//                HashMap<String, String[]> currMap = new HashMap<>();
//                currMap.put(curr, currVar);
//                currSentence.addAll(sentence);
            }else{
                curr = query.get(i).func.substring(1);

//                HashMap<String, String[]> currMap = new HashMap<>();
//                currMap.put(curr, currVar);
//                currSentence.addAll(sentence);
            }
//            HashMap<String, String[]> currMap = new HashMap<>();
//            currMap.put(query.get(i).func, query.get(i).var);
            currSentence.addAll(sentence);

            Query currQ = new Query(curr, currVar);
            Stack<Query> stack = new Stack<>();
            stack.push(currQ);
            HashSet<String> hs = new HashSet<>();
            hs.add(currQ.toString());
            results[i] = dfsQuery(stack, currSentence, hs);      //first unify
        }

        return results;
    }

    private static boolean dfsQuery(Stack<Query> stack, ArrayList<HashMap<String, String[]>> sentence,HashSet<String> hs){

        while(!stack.isEmpty()){
            Query currQQ = stack.pop();
            String newKey;
            String[] currQVar = currQQ.var;
            if(currQQ.func.charAt(0) != '~'){
                newKey = '~' + currQQ.func;
            }else{
                newKey = currQQ.func.substring(1);
            }
            Query curr = new Query(newKey, currQVar);



            for(int index = 0; index < sentence.size(); index++){
                ArrayList<String> variable = new ArrayList<>();
                ArrayList<String> constant = new ArrayList<>();

                if(!sentence.get(index).containsKey(curr.func)) continue;   //if no func found, next

                String[] currVar = sentence.get(index).get(curr.func);      //if found, get its variables
                boolean isMatch = true;
                for(int i = 0; i < currVar.length; i++){
                    if(Character.isUpperCase(currVar[i].charAt(0)) && Character.isUpperCase(curr.var[i].charAt(0))
                            && !currVar[i].equals(curr.var[i])){
                        isMatch = false;                                    // both constant but not equal
                        break;
                    }
                }

                if(isMatch){

                    for(int i = 0; i < currVar.length; i++){                //if match, find the var and constant pair
                        if(Character.isUpperCase(currVar[i].charAt(0)) && !Character.isUpperCase(curr.var[i].charAt(0))) {
                            variable.add(curr.var[i]);
                            constant.add(currVar[i]);
                        }else if(!Character.isUpperCase(currVar[i].charAt(0)) && Character.isUpperCase(curr.var[i].charAt(0))){
                            variable.add(currVar[i]);
                            constant.add(curr.var[i]);
                        }
                    }

//                    HashMap<String, String[]> dead = new HashMap<>();
//                    dead = sentence.get(index); //store the sentence removed
                    //int previous = stack.size();
                    for (Map.Entry<String, String[]> entrySet: sentence.get(index).entrySet()) {
                        if(curr.func.equals(entrySet.getKey())){continue;}       //skip the resloved one  是否只包含一个func？？？？
                        String[] vSet = new String[entrySet.getValue().length];
                        int count = 0;
                        for(String s : entrySet.getValue()){
                            vSet[count++] = s;
                        }

                        for (int j = 0; j < vSet.length; j++) {
                            for(int i = 0; i < variable.size(); i++) {
                                if (vSet[j].equals(variable.get(i))) {
                                    vSet[j] = constant.get(i);                  //unify other funcs in the sentence
                                }
                            }
                        }

                        Query newGuy = new Query(entrySet.getKey(), vSet);

                        StringBuilder SB = new StringBuilder();
                        SB.append(newGuy.toString());
                        SB.append(curr.toString());
                        String newGuyS = SB.toString();


                        if(!hs.contains(newGuyS)){
                            stack.push(newGuy);
                            hs.add(newGuyS);
                        }else{
                            return false;
                        }

                        //sentence.get(index).put(entrySet.getKey(), vSet);   //update the sentence
                    }


//                    ArrayList<HashMap<String, String[]>> currSentence = new ArrayList<>();
//                    currSentence.addAll(sentence);
//                    if(sentence.get(index).size() != 1) currSentence.remove(index);                 // remove the chosen sentence from KB

//                    if(stack.size() == previous){
//                        index = sentence.size();
////                        break;
//                    }

                    if(dfsQuery(stack, sentence, hs)){
                        return true;
                    } else{

                        //while(!stack.isEmpty() && stack.peek().num == numUnify){stack.pop();}//删除此次加入的query，还原本次使用的sentence
                        //stack.add(curr);
                        if(index == sentence.size() - 1) return false;
                    }
                }
            }

            //while(!stack.isEmpty() && stack.peek().num == curr.num){stack.pop();}
            stack.add(curr);
            return false;       //after check all sentences, no match, return false
        }

        if(stack.isEmpty()){
            return true;
        }

        return false;
//        if(index == sentence.size()){
//            return false;
//        }


    }
}
