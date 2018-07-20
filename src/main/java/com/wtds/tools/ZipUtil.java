package com.wtds.tools;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

public class ZipUtil {

	public static void Zip(String source, String target) {
		File srcFile = new File(source);// 源文件
		File desFile = new File(target);// 目标zip文件
		Project project = new Project();
		Zip zip = new Zip();
		zip.setProject(project);
		zip.setDestFile(desFile);
		FileSet fileSet = new FileSet();
		fileSet.setProject(project);
		if (srcFile.isFile()) {
			fileSet.setFile(srcFile);
		} else if (srcFile.isDirectory()) {
			fileSet.setDir(srcFile);
		}
		// fileSet.setIncludes("**/*.java"); //包含哪些文件或文件夹
		// eg:zip.setIncludes("*.java")
		// fileSet.setExcludes(...); //排除哪些文件或文件夹
		zip.addFileset(fileSet);
		zip.execute();
	}

}