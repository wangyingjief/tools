package com.wtds.tools.jshortcut;

import net.jimmc.jshortcut.JShellLink;

/**
 * 快捷方式帮助类
 * 
 * @author wyj
 */
public class ShellLink {
	/**
	 * 创建桌面快捷方式
	 * 
	 * @param path
	 *            需被创建的文件路径
	 * @param name
	 *            创建后的快捷方式名称
	 */
	public static void create(String path, String name) {
		JShellLink link = new JShellLink();
		link.setFolder(JShellLink.getDirectory("desktop"));
		link.setName(name);
		link.setPath(path);
		link.save();
	}

	public static void main(String[] args) {
		ShellLink.create("C:\\wtds\\datadownload\\FileZilla", "W");
	}
}
