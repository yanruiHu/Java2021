package lab7;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class UndefinedException extends Exception{
	UndefinedException(){
		super("wrong - variable undefined");
	}
}

class WrongExpressionException extends Exception{
	WrongExpressionException(){
		super("wrong - error expression");
	}
}

class UnassignedException extends Exception{
	UnassignedException(){
		super("wrong - variable unassigned");
	}
}

public class logger {
	static Logger mylogger=Logger.getLogger("MyLogger");
	static String op;
	static HashMap<Character,Integer> priority=new HashMap<Character,Integer>();
	static {
		op="+-*/%()=?";
		priority.put('+',1);
		priority.put('-',1);
		priority.put('*',2);
		priority.put('/',2);
		priority.put('%',2);
		priority.put('(',0);
		
		try {
			mylogger.setLevel(Level.CONFIG);
			mylogger.setUseParentHandlers(false);
			FileHandler fh=new FileHandler("E://MyLog.txt",true);
			SimpleFormatter sf=new SimpleFormatter();
			fh.setFormatter(sf);
			mylogger.addHandler(fh);
			mylogger.log(Level.CONFIG,"test config");
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void cal(LinkedList<Character> operator,LinkedList<Object> value) {
		char opNow=operator.getFirst();
		operator.removeFirst();
		
		Object v1=value.getFirst();
		value.removeFirst();
		Object v2=value.getFirst();
		value.removeFirst();
		
		if(v1.getClass()==Integer.class&&v2.getClass()==Integer.class) {
			Integer result;
			switch(op.indexOf(opNow)) {
			case 0:result=(Integer)v2+(Integer)v1;break;
			case 1:result=(Integer)v2-(Integer)v1;break;
			case 2:result=(Integer)v2*(Integer)v1;break;
			case 3:result=(Integer)v2/(Integer)v1;break;
			default:result=(Integer)v2%(Integer)v1;break;
			}
			value.addFirst(result);
		}
		else if(v1.getClass()==Integer.class&&v2.getClass()==Float.class) {
			Float result;
			switch(op.indexOf(opNow)) {
			case 0:result=(Float)v2+(Integer)v1;break;
			case 1:result=(Float)v2-(Integer)v1;break;
			case 2:result=(Float)v2*(Integer)v1;break;	
			case 3:result=(Float)v2/(Integer)v1;break;
			default:result=(Float)v2%(Integer)v1;break;
			}
			value.addFirst(result);
		}
		else if(v1.getClass()==Float.class&&v2.getClass()==Integer.class) {
			Float result;
			switch(op.indexOf(opNow)) {
			case 0:result=(Integer)v2+(Float)v1;break;
			case 1:result=(Integer)v2-(Float)v1;break;
			case 2:result=(Integer)v2*(Float)v1;break;
			case 3:result=(Integer)v2/(Float)v1;break;
			default:result=(Integer)v2%(Float)v1;break;
			}
			value.addFirst(result);
		}
		else {
			Float result;
			switch(op.indexOf(opNow)) {
			case 0:result=(Float)v2+(Float)v1;break;
			case 1:result=(Float)v2-(Float)v1;break;
			case 2:result=(Float)v2*(Float)v1;break;
			case 3:result=(Float)v2/(Float)v1;break;
			default:result=(Float)v2%(Float)v1;break;
			}
			value.addFirst(result);
		}
		
	}
	
	static void calculate (String formula)throws Exception{
		LinkedList<Character> operator =new LinkedList<Character>();
		LinkedList<Object> value=new LinkedList<Object>();
		
		int i=0;
		int len=formula.length();
		while(i<len) {
			if(op.contains(Character.toString(formula.charAt(i)))) {
				Character opNow=formula.charAt(i);
				
				if(opNow=='='||opNow=='?') {
					i++;
				}
				else if(opNow==')') {	
					while(operator.getFirst()!='(') {
						cal(operator,value);
					}
					operator.removeFirst();
					i++;
				}
				else if(formula.charAt(i)=='(') {
					operator.addFirst(opNow);
					i++;
				}
				else {
					int priNow=priority.get(opNow),priTop=-1;
					if(!operator.isEmpty()) {
						priTop=priority.get(operator.getFirst());
					}
					while(!operator.isEmpty()&&priNow<=priTop) {
						cal(operator,value);
						if(!operator.isEmpty()) {
							priTop=priority.get(operator.getFirst());
						}
					}
					operator.addFirst(opNow);
					i++;
				}
			}
			else {
				int j=i+1;
				while(j<len&&!op.contains(Character.toString(formula.charAt(j)))) {
					j++;
				}
				String num=formula.substring(i,j);
				if(num.contains(".")) {
					value.addFirst(Float.parseFloat(num));
				}
				else{
					value.addFirst(Integer.parseInt(num));
				}
				i=j;
			}
		}
		while(!operator.isEmpty()) {
			cal(operator,value);
		}
		if(value.getFirst().getClass()==Float.class) {
			System.out.println(String.format("%.2f",value.getFirst()));
		}
		else 
			System.out.println(value.getFirst());
	}
	
	public static void main(String[] args) {
		Scanner scan=new Scanner(System.in);
		HashMap<String,Object> vars=new HashMap<String,Object>();
		HashMap<String,String> types=new HashMap<String,String>();
		String s;
		try {
			
		while((s=scan.nextLine())!=null) {
			if(s.contains("?")) {
				for(String vr:vars.keySet()) {
					if(types.containsKey(vr))
						types.remove(vr);
				}
				
				s=s.substring(0,s.length()-1);
				int i=0;
				while(i<s.length()) {
					if(!op.contains(Character.toString(s.charAt(i)))&&(s.charAt(i)<'0'||s.charAt(i)>'9')&&s.charAt(i)!='.') {
						int j=i+1;
						while(!op.contains(Character.toString(s.charAt(j)))) {
							j++;
						}
						String var=s.substring(i,j);
//System.out.println(var+" "+vars.get(var));
						try {
							String val=vars.get(var).toString();
							s=s.replaceAll(var,val);
							i+=val.length();
						}catch(Exception e) {
							if(types.containsKey(var)) {
								throw new UnassignedException();
							}
							else throw new UndefinedException();
						}
					}
					else {
						i++;
					}
				}
				try {
				calculate(s);
				}catch(Exception e) {
					throw new WrongExpressionException();
				}finally {
					return;
				}
			}
			else {
				s=s.substring(0,s.length()-1);
				String[] input=s.split(" ");
				int size=input.length;
				String type=null,define=null;
				if(size==2) {
					type=input[0];
					define=input[1];
				}
				else {
					define=input[0];
				}
				
				String[] name_value=define.split("=");
				int sz=name_value.length;
				String name,value=null;
				if(sz==2) {
					name=name_value[0];
					value=name_value[1];
				}
				else {
					name=name_value[0];
				}
				
				if(type!=null&&value!=null) {
					if(type.equals("int")) {
//System.out.println("vars.put "+name+" "+value);
						vars.put(name, Integer.parseInt(value));
					}
					else {
//System.out.println("vars.put "+name+" "+value);
						vars.put(name, Float.parseFloat(value));
					}
				}
				else if(type!=null&&value==null) {
//System.out.println("types.put "+name+" "+type);
					types.put(name, type);
				}
				else {
					String tp=types.get(name);
					if(tp.equals("int")) {
//System.out.println("vars.put "+name+" "+value);
						vars.put(name, Integer.parseInt(value));
					}
					else {
//System.out.println("vars.put "+name+" "+value);
						vars.put(name, Float.parseFloat(value));
					}
				}
				
			}
		}
		}catch (Exception e) {
			mylogger.warning(e.getMessage());
		}finally {
			scan.close();
		}
	}
}
