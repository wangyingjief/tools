package com.wtds.tools;



import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * 处理ajax请求帮助类
 * @author joymting
 *
 */
public class AjaxHelper {

	/**
	 * 通过ajax向页面打印文本提示 print
	 * 设置响应文本类型，编码
	 * 响应完后关闭流
	 * @param response
	 * @param responseText
	 * @return
	 */
	public static void  printString(HttpServletResponse response,String responseText){
		PrintWriter out = null;
		try{
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.print(responseText);
			out.flush();
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
	}
	
	/**
	 * 通过ajax向页面打印文本提示 println
	 * 设置响应文本类型，编码
	 * 响应完后关闭流
	 * @param response
	 * @param responseText
	 * @return
	 */
	public static void  printlnString(HttpServletResponse response,String responseText){
		PrintWriter out = null;
		try{
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.println(responseText);
			out.flush();
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
	}
	
	/**
	 * 通过ajax向页面打印文本Json
	 * 设置响应文本类型，编码
	 * 响应完后关闭流
	 * @param response
	 * @param responseText
	 * @return
	 */
	public static void  printJson(HttpServletResponse response,String responseText){
		PrintWriter out = null;
		try{
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.print(responseText);
			out.flush();
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
	}
}