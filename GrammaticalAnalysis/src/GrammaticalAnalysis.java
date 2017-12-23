import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class GrammaticalAnalysis extends JFrame {
    public static void main(String args[]){
        GrammaticalAnalysis g = new GrammaticalAnalysis();
        g.setVisible(true);
    }

    public GrammaticalAnalysis() {
        String s = "";
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();  //三维结构存储非终结符和产生式
        ArrayList<String> newArray = new ArrayList<String>();
        int readByte;
        char dataChar, charTemp;
        boolean flag = false;
        int pace = 0;  //步骤计数

        try {
            File f = new File("/home/brunon/IdeaProjects/GrammaticalAnalysis", "name.txt");
            FileInputStream in = new FileInputStream(f);
            readByte = in.read();  dataChar = (char)readByte;
            while(-1 != readByte) {
                s += dataChar;
                for(int i=0; i<list.size(); i++) {
                    newArray = list.get(i);
                    if(newArray.get(0).equals(s)){
                        flag = true;
                        s = "";
                        break;
                    }
                }
                if(!flag) {
                    newArray = new ArrayList<String>(); //动态分配内存对新的非终结符和产生式读入
                    newArray.add(s);
                    s = "";
                }
                if('-' == (char)in.read()) {
                    if('>' == (char)in.read()) {
                        while('\n' != dataChar) {
                            readByte = in.read();
                            dataChar = (char)readByte;
                            if(-1 == readByte || '\n' == dataChar) {
                                readByte = in.read(); dataChar = (char)readByte;
                                break;
                            }
                            if('|' == dataChar){
                                newArray.add(s);
                                s = "";
                                readByte = in.read(); dataChar = (char)readByte;
                            }
                            s += dataChar;
                        }
                        newArray.add(s);
                        s = "";
                    }
                }
                if(!flag) {
                    list.add(newArray);
                }
                flag = false;
            }
        }
        catch (IOException e) {
            System.out.println("File read error!" + e);
        }

        /**part0.产生式**/
        String[][] tableValues = new String[list.size()][2];
        for(int i=0; i<list.size(); i++) {
            tableValues[i][0] = list.get(i).get(0);
            tableValues[i][1] = "";
            for(int j=1; j<list.get(i).size(); j++) {
                tableValues[i][1] += list.get(i).get(j) + " ";
            }
        }
        String[] columnNames = {"表1.非终结符", "产生式"};
        JTable table = new JTable(tableValues, columnNames);
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, r);
        table.setFont(new Font("Menu.font", Font.PLAIN, 20));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        /**part1.Frist集**/
        String[][] tableValues1 = new String[list.size()][2];
        for(int i=0; i<list.size(); i++) {
            tableValues1[i][0] = tableValues[i][0];
            tableValues1[i][1] = "";
            for(String name : getFrist(tableValues[i][0], list)) {
                tableValues1[i][1] += name;
            }
        }
        String[] columnNames1 = {"表2.非终结符", "Frist集"};
        JTable table1 = new JTable(tableValues1, columnNames1);
        table1.setDefaultRenderer(Object.class, r);
        table1.setFont(new Font("Menu.font", Font.PLAIN, 20));
        table1.setRowHeight(30);
        JScrollPane scrollPane1 = new JScrollPane(table1);

        String[][] tableValues2 = new String[list.size()][2];    //part2.Follow集
        for(int i=0; i<list.size(); i++) {
            tableValues2[i][0] = tableValues[i][0];
            tableValues2[i][1] = "";
            for(String name : getFollow(tableValues[i][0], list)) {
                tableValues2[i][1] += name;
            }
        }
        String[] columnNames2 = {"表3.非终结符", "Follow集"};
        JTable table2 = new JTable(tableValues2, columnNames2);
        table2.setDefaultRenderer(Object.class, r);
        table2.setFont(new Font("Menu.font", Font.PLAIN, 20));
        table2.setRowHeight(30);
        JScrollPane scrollPane2 = new JScrollPane(table2);

        /**part3.预测分析表**/
        Set<String> endChar = new HashSet<String>();
        endChar.add("产生式");
        endChar.add("#");
        for(int i=0; i<list.size(); i++) {
            for(int j=1; j<list.get(i).size(); j++) {
                for(int k=0; k<list.get(i).get(j).length(); k++) {
                    if(-1 == IsEndchar(String.valueOf(list.get(i).get(j).charAt(k)), list) &&
                            list.get(i).get(j).charAt(k) != '$') {
                        endChar.add(String.valueOf(list.get(i).get(j).charAt(k)));
                    }
                }
            }
        }
        String[] columnNames3 = new String[endChar.size()];
        Set<String> frist = new HashSet<String>();
        Set<String> follow = new HashSet<String>();
        endChar.toArray(columnNames3);
        String[][] tableValues3 = new String[list.size()][endChar.size()];
        for(int i=0; i<list.size(); i++) {
            tableValues3[i][0] = tableValues[i][0];
            tableValues3[i][1] = "";
            for(int j=1; j<list.get(i).size(); j++) {
                frist = getFrist(String.valueOf(list.get(i).get(j).charAt(0)), list);
                for(String i0 : frist) {
                    for(int index=1; index<endChar.size(); index++) {
                        if(i0.equals(columnNames3[index] + " ")) {
                            tableValues3[i][index] = "";
                            tableValues3[i][index] += list.get(i).get(0);
                            tableValues3[i][index] += "->";
                            tableValues3[i][index] += list.get(i).get(j);
                            break;
                        }
                    }
                }
                if(frist.contains("$ ")) {
                    follow = getFollow(list.get(i).get(0), list);
                    for(String i0 : follow) {
                        for(int index=1; index<endChar.size(); index++) {
                            if(i0.equals(columnNames3[index] + " ")) {
                                tableValues3[i][index] = "";
                                tableValues3[i][index] += list.get(i).get(0);
                                tableValues3[i][index] += "->";
                                tableValues3[i][index] += list.get(i).get(j);
                                break;
                            }
                        }
                    }
                }
            }
        }
        JTable table3 = new JTable(tableValues3, columnNames3);
        table3.setDefaultRenderer(Object.class, r);
        table3.setFont(new Font("Menu.font", Font.PLAIN, 20));
        table3.setRowHeight(30);
        JScrollPane scrollPane3 = new JScrollPane(table3);

        /**part4.语法分析**/
        String[][] tableValues4 = new String[30][5];
        String[] columnNames4 = {"步骤", "分析栈", "剩余字符串", "所用产生式", "动作"};
        String inputWrod = ")i+i*i#";
        Stack<String> input = new Stack<String>();
        for(int i=inputWrod.length()-1; i>=0; i--) {
            input.push(String.valueOf(inputWrod.charAt(i)));
        }
        Stack<String> myStack = new Stack<String>();
        myStack.push("#");
        myStack.push(list.get(0).get(0));    //init
        tableValues4[pace][0] = String.valueOf(pace);
        tableValues4[pace][1] = "";
        tableValues4[pace][1] += myStack.toString();
        tableValues4[pace][2] = input.toString();
        tableValues4[pace][3] = "";
        tableValues4[pace][4] = "初始化";
        pace++;
        while(true) {
            if(-1 != IsEndchar(myStack.lastElement(), list)) {   //X是非终结符
                tableValues4[pace][0] = String.valueOf(pace);
                for(int i=1; i<endChar.size(); i++) {   //对所要使用到的产生式进行定位
                    for(int j=0; j<list.size(); j++) {
                        if(myStack.lastElement().equals(list.get(j).get(0))
                                && input.lastElement().equals(columnNames3[i])) {
                            tableValues4[pace][3] = tableValues3[j][i];
                            break;
                        }
                    }
                }
                if(null == tableValues4[pace][3]) {   //判断是否有得到对应的产生式
                    input.pop();
                    tableValues4[pace][1] = myStack.toString();
                    tableValues4[pace][2] = input.toString();
                    tableValues4[pace][4] = "Error, next";
                    pace++;
                    continue;
                }
                myStack.pop();
                for(int i=tableValues4[pace][3].length()-1;
                    !String.valueOf(tableValues4[pace][3].charAt(i)).equals(">"); i--) {
                    if(!String.valueOf(tableValues4[pace][3].charAt(i)).equals("$")) {
                        myStack.push(String.valueOf(tableValues4[pace][3].charAt(i)));
                        tableValues4[pace][4] = "pop, push";
                    }
                }
                tableValues4[pace][1] = myStack.toString();
                tableValues4[pace][2] = input.toString();
                if(null == tableValues4[pace][4]) {
                    tableValues4[pace][4] = "pop";
                }
                pace++;
                continue;
            }
            if(myStack.lastElement().equals(input.lastElement())) {
                if(myStack.lastElement().equals("#")) {    //结束条件
                    break;
                }
                tableValues4[pace][0] = String.valueOf(pace);
                myStack.pop();
                input.pop();
                tableValues4[pace][1] = myStack.toString();
                tableValues4[pace][2] = input.toString();
                tableValues4[pace][4] = "GetNext";
                pace++;
            }
        }
        JTable table4 = new JTable(tableValues4, columnNames4);
        table4.setDefaultRenderer(Object.class, r);
        table4.setFont(new Font("Menu.font", Font.PLAIN, 20));
//        table4.setBounds(400, 600, 1200, 200);
        table4.setRowHeight(30);
        JScrollPane scrollPane4 = new JScrollPane(table4);

        /**总体布局**/
        setTitle("GarammaticalAnalysis");
        setBounds(400, 200, 1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        table.setBounds(400, 200, 400, 200);
        getContentPane().add(scrollPane, BorderLayout.WEST);
        getContentPane().add(scrollPane1, BorderLayout.CENTER);
        getContentPane().add(scrollPane2, BorderLayout.EAST);
        getContentPane().add(scrollPane3, BorderLayout.NORTH);
        getContentPane().add(scrollPane4, BorderLayout.SOUTH);
    }

    public int IsEndchar(String s, ArrayList<ArrayList<String>> list){     //判断是否是终结符
        for(int i=0; i<list.size(); i++) {
            if(list.get(i).get(0).equals(s)) {
                return i;
            }
        }
        return -1;
    }

    public Set<String> getFrist(String s, ArrayList<ArrayList<String>> list) {   //求Frist集
        Set<String> frist = new HashSet<String>();
        int index = IsEndchar(s, list);
        if(-1 == index) {
            frist.add(s + " ");
            return frist;
        }
        else {
            for(int j=1; j<list.get(index).size(); j++) {
                frist.addAll(getFrist(String.valueOf(list.get(index).get(j).charAt(0)), list));
            }
        }
        return frist;
    }

    public Set<String> getFollow(String s, ArrayList<ArrayList<String>> list) {  //求Follow集
        Set<String> follow = new HashSet<String>();
        int index = IsEndchar(s, list);
        int temp;
        if(0 == index) {
            follow.add("# ");
        }
        for(int i=0; i<list.size(); i++) {  //遍历所有产生式
            for(int j=1; j<list.get(i).size(); j++) { //遍历每个非终结符的产生式
                for(int k=0; k<list.get(i).get(j).length(); k++) { //遍历每个产生式的字母
                    if(s.equals(String.valueOf(list.get(i).get(j).charAt(k)))) {
                        if(k == list.get(i).get(j).length()-1) {
                            if(!list.get(i).get(0).equals(s)) {
                                follow.addAll(getFollow(list.get(i).get(0), list));
                            }
                        }
                        else {
                            temp = IsEndchar(String.valueOf(list.get(i).get(j).charAt(k+1)), list);
                            if(temp == -1) {   //若所求非终结符的follow是一个终结符,则选中
                                follow.add(String.valueOf(list.get(i).get(j).charAt(k+1)) + " ");
                            }
                            else {  //若所求非终结符的follow是一个产生式
                                String notEnd = String.valueOf(list.get(i).get(j).charAt(k+1));
                                Set<String> frist = getFrist(notEnd, list);
                                boolean search = frist.contains("$ ");
                                if(search) {
                                    follow.addAll(getFollow(notEnd, list));
                                }
                                frist.remove("$ ");
                                follow.addAll(frist);
                            }
                        }
                    }
                }
            }
        }
        return follow;
    }
}
