import org.omg.CORBA.StringHolder;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class LR extends JFrame {
    public static void main(String args[]){
        LR g = new LR();
        g.setVisible(true);
    }

    public LR() {
        String s = "";
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();  //三维结构存储非终结符和产生式
        ArrayList<String> newArray = new ArrayList<String>();
        int readByte;
        char dataChar, charTemp;
        boolean flag = false;

        try {
            File f = new File("/home/brunon/IdeaProjects/LR", "name.txt");
            FileInputStream in = new FileInputStream(f);
            readByte = in.read();
            dataChar = (char) readByte;
            while (-1 != readByte) {
                s += dataChar;
                for (int i = 0; i < list.size(); i++) {
                    newArray = list.get(i);
                    if (newArray.get(0).equals(s)) {
                        flag = true;
                        s = "";
                        break;
                    }
                }
                if (!flag) {
                    newArray = new ArrayList<String>(); //动态分配内存对新的非终结符和产生式读入
                    newArray.add(s);
                    s = "";
                }
                if ('-' == (char) in.read()) {
                    if ('>' == (char) in.read()) {
                        while ('\n' != dataChar) {
                            readByte = in.read();
                            dataChar = (char) readByte;
                            if (-1 == readByte || '\n' == dataChar) {
                                readByte = in.read();
                                dataChar = (char) readByte;
                                break;
                            }
                            if ('|' == dataChar) {
                                newArray.add(s);
                                s = "";
                                readByte = in.read();
                                dataChar = (char) readByte;
                            }
                            s += dataChar;
                        }
                        newArray.add(s);
                        s = "";
                    }
                }
                if (!flag) {
                    list.add(newArray);
                }
                flag = false;
            }
        } catch (IOException e) {
            System.out.println("File read error!" + e);
        }

        /**part0.产生式**/
        String[][] tableValues = new String[list.size()][2];
        for (int i = 0; i < list.size(); i++) {
            tableValues[i][0] = list.get(i).get(0);
            tableValues[i][1] = "";
            for (int j = 1; j < list.get(i).size(); j++) {
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

        /**part1.项目**/
        ArrayList<String[]> projectList = new ArrayList<String[]>();
        String[] pepo = new String[2];
        pepo[0] = list.get(0).get(0) + "'";
        pepo[1] = "." + list.get(0).get(0);
        projectList.add(pepo);
        pepo = new String[2];
        pepo[0] = list.get(0).get(0) + "'";
        pepo[1] = list.get(0).get(0) + ".";
        projectList.add(pepo);
        for (ArrayList<String> i : list) {
            for (int j = 1; j < i.size(); j++) {
                for (int k = 0; k < i.get(j).length() + 1; k++) {
                    pepo = new String[2];
                    pepo[0] = i.get(0);
                    pepo[1] = "";
                    for (int l = 0; l < i.get(j).length(); l++) {
                        if (l == k) pepo[1] += ".";
                        pepo[1] += i.get(j).charAt(l);
                    }
                    if (k == i.get(j).length()) pepo[1] += ".";
                    projectList.add(pepo);
                }
            }
        }
        String[][] tableValues1 = new String[projectList.size()][2];
        for (int i = 0; i < projectList.size(); i++) {
            for (int j = 0; j < 2; j++) {
                tableValues1[i][j] = projectList.get(i)[j];
            }
        }
        String[] columnNames1 = {"表2.非终结符", "项目"};
        JTable table1 = new JTable(tableValues1, columnNames1);
        table1.setDefaultRenderer(Object.class, r);
        table1.setFont(new Font("Menu.font", Font.PLAIN, 20));
        table1.setRowHeight(30);
        JScrollPane scrollPane1 = new JScrollPane(table1);

        /**part2.项目集簇**/
        ArrayList<String[]> l0 = new ArrayList<String[]>();    //初始项目集l0
        l0.add(tableValues1[0]);
        for (int i = 0; i < l0.size(); i++) {
            CLOSURE(l0, l0.get(i), tableValues1);
        }
        String target = "";
        flag = false;
        ArrayList<ArrayList<String[]>> groupList = new ArrayList<ArrayList<String[]>>();    //项目集簇
        groupList.add(l0);
        for (int i = 0; i < groupList.size(); i++) {    //求全部的项目集,对每个项目集求产生的项目集
            for (int k = 0; k < groupList.get(i).size(); k++) {             //对每个项目求产生的项目集
                for (int j = 0; j < groupList.get(i).get(k)[1].length() - 1; j++) {   //取.后面的字符
                    if (groupList.get(i).get(k)[1].charAt(j) == '.') {
                        target = String.valueOf(groupList.get(i).get(k)[1].charAt(j + 1));
                    }
                }
                l0 = GO(groupList.get(i), target, tableValues1);
                for (int f = 0; f < groupList.size(); f++) {
                    if (groupList.get(f).equals(l0)) flag = true;
                }
                if (!flag && l0.size() != 0) {
                    groupList.add(l0);
                }
                flag = false;
            }
        }

        /**part3.LR(0)分析表**/
        ArrayList<String> formHead = new ArrayList<String>();
        formHead.add("表2.状态");
        formHead.add("#");
        Set<String> ACTION = new HashSet<String>();
        Set<String> GOTO = new HashSet<String>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 1; j < list.get(i).size(); j++) {
                for (int k = 0; k < list.get(i).get(j).length(); k++) {
                    if (-1 == IsEndchar(String.valueOf(list.get(i).get(j).charAt(k)), list) &&
                            list.get(i).get(j).charAt(k) != '$') {
                        ACTION.add(String.valueOf(list.get(i).get(j).charAt(k)));
                    }
                }
            }
        }
        for (String i : ACTION) {
            formHead.add(i);
        }
        int action = ACTION.size();    //记录ACTION个数
        for (int i = 0; i < list.size(); i++) {
            for (int j = 1; j < list.get(i).size(); j++) {
                for (int k = 0; k < list.get(i).get(j).length(); k++) {
                    if (-1 != IsEndchar(String.valueOf(list.get(i).get(j).charAt(k)), list) &&
                            list.get(i).get(j).charAt(k) != '$') {
                        GOTO.add(String.valueOf(list.get(i).get(j).charAt(k)));
                    }
                }
            }
        }
        int myGoto = GOTO.size();    //记录GOTO个数
        for (String i : GOTO) {
            formHead.add(i);
        }
        String[][] form = new String[groupList.size()][formHead.size()];
        flag = false;
        int rNumber = 0;
        s = "";
        for (int state = 0; state < groupList.size(); state++) {
            form[state][0] = String.valueOf(state);
            for (String[] rr : groupList.get(state)) {    //判断该项目集中是否有A->a.的项目
                if ('.' == rr[1].charAt(rr[1].length() - 1)) {   //若有
                    for (int ii = 0; ii < rr[1].length() - 1; ii++) { //取出该产生式，并查找序号
                        s += String.valueOf(rr[1].charAt(ii));
                    }
                    for (ArrayList<String> sss : list) {
                        for (int iii = 1; iii < sss.size(); iii++) {
                            if (sss.get(iii).equals(s)) {     //找到了产生式序号
                                s = sss.get(0);
                                flag = true;
                            }
                            if (flag) break;
                            else rNumber++;
                        }
                        if (flag) break;
                    }
                    Set<String> follow = getFollow(s, list);      //对SLR作不同的处理
                    s = "r" + String.valueOf(rNumber);
                    for (int jj = 1; jj < 2 + action; jj++) {
                        if (follow.contains(formHead.get(jj) + " "))
                            form[state][jj] = s;
                    }
                    s = "";
                    rNumber = 0;
                    break;
                }
            }

            if (true) {
                for (String[] search : groupList.get(state)) {
                    if (search[0].equals(projectList.get(1)[0]) && search[1].equals(projectList.get(1)[1])) {
                        form[state][1] = "acc";
                    }
                }
                for (int i = 2; i < action + 2; i++) {    //对ACTION处理
                    l0 = GO(groupList.get(state), formHead.get(i), tableValues1);
                    for (int index = 0; index < groupList.size(); index++) {
                        if (l0.equals(groupList.get(index))) {
                            s = "s" + String.valueOf(index);
                            form[state][i] = s;
                            s = "";
                            break;
                        }
                    }
                }
            }
            flag = false;

            //对GOTO处理
            for (int i = 2 + action; i < formHead.size(); i++) {    //对ACTION处理
                l0 = GO(groupList.get(state), formHead.get(i), tableValues1);
                for (int index = 0; index < groupList.size(); index++) {
                    if (l0.equals(groupList.get(index))) {
                        s = String.valueOf(index);
                        form[state][i] = s;
                        s = "";
                        break;
                    }
                }
                int index = groupList.indexOf(l0);
            }
        }
        String[] columnNames2 = new String[formHead.size()];
        for (int i = 0; i < formHead.size(); i++) {
            columnNames2[i] = formHead.get(i);
        }
        JTable table2 = new JTable(form, columnNames2);
        table2.setDefaultRenderer(Object.class, r);
        table2.setFont(new Font("Menu.font", Font.PLAIN, 20));
        table2.setRowHeight(30);
        JScrollPane scrollPane2 = new JScrollPane(table2);

        /**part4.分析过程**/
        int pace = 0;  //步骤计数
        String[][] tableValues4 = new String[30][6];
        String[] columnNames4 = {"步骤", "状态栈", "符号栈", "剩余字符串", "所用产生式", "动作"};
        String inputWrod = ")i+i*i#";
//        String inputWrod = "aabb";
        Stack<String> input = new Stack<String>();
        for(int i=inputWrod.length()-1; i>=0; i--) {
            input.push(String.valueOf(inputWrod.charAt(i)));
        }
        Stack<String> stateStack = new Stack<String>();//状态栈
        Stack<String> myStack = new Stack<String>();   //符号栈
        myStack.push("#");
        stateStack.push("0");    //init
        tableValues4[pace][0] = String.valueOf(pace);
        tableValues4[pace][1] = stateStack.toString();
        tableValues4[pace][2] = myStack.toString();
        tableValues4[pace][3] = input.toString();
        tableValues4[pace][4] = "";
        tableValues4[pace][5] = "初始化";
        pace++;
        while(true) {
            tableValues4[pace][0] = String.valueOf(pace);
            int loction = 1;
            for(int i=2; i<action+2; i++) {
                if(input.lastElement().equals(formHead.get(i))) {
                    loction = i;
                }
            }
            String cmd = form[Integer.valueOf(stateStack.lastElement())][loction];
            if(null == cmd) {
                tableValues4[pace][1] = stateStack.toString();
                tableValues4[pace][2] = myStack.toString();
                input.pop();
                tableValues4[pace][3] = input.toString();
                tableValues4[pace][4] = "";
                tableValues4[pace][5] = "Error!";
                pace++;
                continue;
            }
            if(cmd.equals("acc")) {
                tableValues4[pace][1] = stateStack.toString();
                tableValues4[pace][2] = myStack.toString();
                tableValues4[pace][3] = input.toString();
                tableValues4[pace][4] = "";
                tableValues4[pace][5] = "Acc:分析成功！";
                break;
            }
            if('s' == cmd.charAt(0)) {    //移进操作
                stateStack.push(String.valueOf(cmd.charAt(1)));
                tableValues4[pace][1] = stateStack.toString();
                myStack.push(input.pop());
                tableValues4[pace][2] = myStack.toString();
                tableValues4[pace][3] = input.toString();
                tableValues4[pace][4] = "";
                tableValues4[pace][5] = "移进，ACTION[" + stateStack.lastElement() + ", " +myStack.lastElement() + "]=" + cmd;
                pace++;
                continue;
            }
            if('r' == cmd.charAt(0)) {   //归约操作
                int search = 0;
                for(int i=0; i<list.size(); i++) {
                    for(int j=1; j<list.get(i).size(); j++) {
                        if(String.valueOf(search).equals(String.valueOf(cmd.charAt(1)))) {
                            s = list.get(i).get(j);
                            for(int k=0; k<s.length(); k++) myStack.pop();
                            myStack.push(list.get(i).get(0));
                        }
                        search++;
                    }
                }
                for(int i=0; i<s.length(); i++) stateStack.pop();
                String temp = stateStack.lastElement();
                for(int i=2+action; i<formHead.size(); i++) {
                    if(myStack.lastElement().equals(formHead.get(i))) {
                        stateStack.push(form[Integer.valueOf(stateStack.lastElement())][i]);
                    }
                }
                tableValues4[pace][1] = stateStack.toString();
                tableValues4[pace][2] = myStack.toString();
                tableValues4[pace][3] = input.toString();
                tableValues4[pace][4] = myStack.lastElement() + "->" + s;
                tableValues4[pace][5] = "归约，GOTO[" + temp + ", " +myStack.lastElement() + "]=" + stateStack.lastElement();
                pace++;
                continue;
            }
        }
        JTable table3 = new JTable(tableValues4, columnNames4);
        table3.setDefaultRenderer(Object.class, r);
        table3.setFont(new Font("Menu.font", Font.PLAIN, 20));
        table3.setRowHeight(30);
        JScrollPane scrollPane3 = new JScrollPane(table3);

        /**总体布局**/
        setTitle("LR");
        setBounds(100, 200, 1400, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        table.setBounds(1550, 200, 400, 200);
        getContentPane().add(scrollPane, BorderLayout.WEST);
        getContentPane().add(scrollPane1, BorderLayout.CENTER);
        getContentPane().add(scrollPane2, BorderLayout.NORTH);
        getContentPane().add(scrollPane3, BorderLayout.SOUTH);

        /**项目集簇GUI**/
        JFrame projectGUI = new JFrame();
        String[][][] gui = new String[groupList.size()][][];
        for(int i=0; i<groupList.size(); i++) {
            gui[i] = new String[groupList.get(i).size()][2];
            for(int j=0; j<groupList.get(i).size(); j++) {
                gui[i][j] = groupList.get(i).get(j);
            }
        }
        String[][] name = new String[groupList.size()][];
        JTable [] t = new JTable[groupList.size()];
        JScrollPane[] sc = new JScrollPane[groupList.size()];
        projectGUI.setBounds(800, 200, 400, 1000);
        projectGUI.setLayout(new GridLayout(0, 2));
        for(int i=0; i<groupList.size(); i++) {
            name[i] = new String[2];
            name[i][0] = "编号" + String.valueOf(i);
            name[i][1] = "容量" + String.valueOf(groupList.get(i).size());
            t[i] = new JTable(gui[i], name[i]);
            t[i].setDefaultRenderer(Object.class, r);
            t[i].setFont(new Font("Menu.font", Font.PLAIN, 20));
            t[i].setRowHeight(25);
            sc[i] = new JScrollPane(t[i]);
            projectGUI.getContentPane().add(sc[i]);
        }
        projectGUI.setVisible(true);
        projectGUI.setTitle("项目集簇");
        projectGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public ArrayList<String[]> GO(ArrayList<String[]> list, String s, String[][] projectAll) {   //相当于GO(list, s) = I(返回的闭包)
        ArrayList<String[]> anotherSet = new ArrayList<String[]>();
        String target = "";
        boolean flag = false;
        for(String[] project : list) {
            for(int i=0; i<project[1].length(); i++) {   //移进
                if(project[1].charAt(i) == '.' && i != project[1].length()-1 && String.valueOf(project[1].charAt(i+1)).equals(s)) {
                    target += s;
                    target += '.';
                    i++;
                    flag = true;
                    continue;
                }
                target += String.valueOf(project[1].charAt(i));
            }
            if(flag) {
                for(int j = 0; j < projectAll.length; j++) {   //从总项目集中寻找符合条件的项目
                    for(int k = 1; k < projectAll[j][1].length(); k++) {
                        if(projectAll[j][1].equals(target)) {
                            anotherSet.add(projectAll[j]);
                            break;
                        }
                    }
                }
                flag = false;
            }
            target = "";
        }
        //下面根据已有的项目求闭包
        for(int i=0; i<anotherSet.size(); i++) {
            CLOSURE(anotherSet, anotherSet.get(i), projectAll);
        }
        return anotherSet;
    }

    public void CLOSURE(ArrayList<String[]> list, String[] project, String[][] projectAll) {   //给一个项目求闭包
        String[] newProject;
        String target = "";
        for(int i=0; i<project[1].length()-1; i++) {    //最后一个字符不需要考虑，是.也没用
            if(project[1].charAt(i) == '.') {
                target = String.valueOf(project[1].charAt(i+1));
            }
        }
        boolean flag = false;
        for(int i=0; i<projectAll.length; i++) {     //从总项目集中寻找符合条件的项目加入
            if(projectAll[i][0].equals(target) && projectAll[i][1].charAt(0) == '.') {
                for(String[] search : list) {
                    if(search[0].equals(projectAll[i][0]) && search[1].equals(projectAll[i][1])) {
                        flag = true;         //判断项目集中已有该项目
                        break;
                    }
                }
                if(!flag) {
                    newProject = projectAll[i];
                    list.add(newProject);
                    flag = false;
                }
            }
        }
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
                if(!list.get(index).get(0).equals(s)) {
                    frist.addAll(getFrist(String.valueOf(list.get(index).get(j).charAt(0)), list));
                }
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

