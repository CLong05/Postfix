import java.io.*;
import java.util.*;

/**
 * 主类，通过调用Parser类，将终端用户输入的一个中缀表达式转换为等价的后缀表达式后输出
 */
public class Postfix {
	/**
	 * 程序主函数，生成一个Parser对象
	 *
	 * @param args 命令行输入参数，但此程序并未用到
	 * @throws IOException the io exception
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Input an infix expression and output its postfix notation:");
		new Parser().expr();
		System.out.println("\nEnd of program.");
	}
}

/**
 * 对输入的中缀表达式进行分析，转换为等价的后缀表达式后输出
 */
class Parser {
	/**
	 * 存储输入的中缀表达式的列表
	 */
	static List<Character> input;
	/**
	 * 存储转换后的后缀表达式的列表
	 */
	static List<Character> result;
	/**
	 * 要分析的下一个字符
	 */
	static int lookahead;
	/**
	 * 要分析的下一个字符在输入的表达式中的索引
	 */
	static int indexOfLookahead;
	static int indexOfPre;

	/**
	 * 构造解析器：创建并初始化相关变量，获取用户输入并进行存储
	 */
	public Parser() {
		/* 创建相关变量并进行初始化 */
		result = new ArrayList<>();
		input = new ArrayList<>();
		indexOfLookahead = 0;
		/* 读取输入 */
		Scanner s = new Scanner(System.in);
		String in = s.nextLine();
		for (int i = 0; i < in.length(); i++) {
			input.add(in.charAt(i));
		}
		System.out.println("======================================================");
		lookahead = input.get(indexOfLookahead);
		s.close();
	}

	/**
	 * 调用term()、rest()函数，对中缀表达式进行转换，并输出转换后的结果
	 *
	 * @throws IOException the io exception
	 */
	void expr() throws IOException {
		term();
		rest();
		System.out.print("The output should be：");
		for (Character character : result) {
			System.out.print(character);
		}
	}

	/**
	 * 检测当前位置是否为合法运算符（+或-）。 若是则调用match与term函数读取下一位分析并将此运算符加入结果列表；否则定位并输出错误信息并自动忽略此字符并读取下一个字符直到遇到+或-
	 *
	 * @throws IOException the io exception
	 */
	void rest() throws IOException {
		while (indexOfLookahead < input.size()) {
			if (Character.isDigit((char) lookahead)) {
				/* 错误类型：两个运算量间缺少运算符 */
				printLocation(indexOfLookahead);
				System.out.println("语法错误，该运算量与前一个运算量之间缺少运算符，已忽略该运算量");
				System.out.println("======================================================");
				indexOfLookahead++;
				if (indexOfLookahead < input.size()) {
					lookahead = input.get(indexOfLookahead);
				}

			} else if (lookahead == ' ') {
				/* 判断输入字符是否为空格，若是则忽略该输入字符，直到读取到一个非空格字符或者是输入表达式结束 */
				stripSpace();
			} else if (lookahead != '+' && lookahead != '-') {
				/* 错误类型：输入非法运算符 */
				printLocation(indexOfLookahead);
				System.out.println("词法错误，非法运算符，已忽略该字符");
				System.out.println("======================================================");
				indexOfLookahead++;
				if (indexOfLookahead < input.size()) {
					lookahead = input.get(indexOfLookahead);
				}
			} else if (lookahead == '+') {
				/* 处理“+”号运算 */
				indexOfPre = indexOfLookahead;
				match('+');
				term();
				if (indexOfLookahead <= input.size())
					result.add('+');

			} else if (lookahead == '-') {
				/* 处理“-”号运算 */
				indexOfPre = indexOfLookahead;
				match('-');
				term();
				if (indexOfLookahead <= input.size())
					result.add('-');

			}
		}
	}

	/**
	 * 检测当前的输入是否为数字。若是数字，则将此数字加入到结果列表中；否则报错并忽略该字符，继续读取下一个字符直到遇到数字。
	 *
	 * @throws IOException the io exception
	 */
	void term() throws IOException {
		/* 判断输入字符是否为数字 */
		while (indexOfLookahead < input.size() && !Character.isDigit((char) lookahead)) {
			/* 判断输入字符是否为空格，若是则调用相应函数进行处理，否则直接报错并忽略该输入 */
			if (lookahead == ' ') {
				stripSpace();
			} else if (lookahead == '+' || lookahead == '-') {
				printLocation(indexOfLookahead);
				if (result.size() == 0) {
					System.out.println("语法错误，缺少左运算量，已忽略此输入字符");
				} else {
					System.out.println("语法错误，前一运算符缺少右运算量，已忽略此输入字符");
				}
				System.out.println("======================================================");
				indexOfLookahead++;
			} else {
				printLocation(indexOfLookahead);
				System.out.println("词法错误，非法运算符，已忽略该字符");
				System.out.println("======================================================");
				indexOfLookahead++;
			}
			/* 判断索引是否越界 */
			if (indexOfLookahead < input.size()) {
				lookahead = input.get(indexOfLookahead);
			}
		}
		/* 若未发生越界，说明已经获得数字字符，则将此数字字符加入到结果列表中 */
		if (indexOfLookahead < input.size()) {
			result.add((char) lookahead);
			match(lookahead);
		} else if (result.size() != 0) {
			printLocation(indexOfPre);
			indexOfLookahead++;
			System.out.println("语法错误，该运算符缺少右运算量，已忽略该运算符字符");
			System.out.println("======================================================");
		}
	}

	/**
	 * 判断当前lookahead与传入的字符是否一致。若一致则读取下一个字符并判断索引是否越界以及下一个字符是否为空格；若越界则停止继续读取；若为空格则调用的函数会进行报错与出错恢复。
	 *
	 * @param t 匹配字符
	 * @throws IOException the io exception
	 */
	void match(int t) throws IOException {
		if (lookahead == t) {
			indexOfLookahead++;
			/* 判断索引是否越界 */
			if (indexOfLookahead < input.size()) {
				lookahead = input.get(indexOfLookahead);
			}
			/* 调用函数判断下一个输入字符是否为空格，若是则进行相应处理 */
			stripSpace();
		} else
			throw new Error("语法错误");
	}

	/**
	 * 打印错误信息，显示出错输入字符在表达式中的位置。
	 *
	 * @param index 当前出错字符在输入列表中的索引
	 */
	void printLocation(int index) {
		for (Character character : input) {
			System.out.print(character);
		}
		System.out.print('\n');
		for (int i = 0; i < index; i++) {
			System.out.print(' ');
		}
		System.out.println('!');
	}

	/**
	 * 判断输入字符是否为空格。若是空格则忽略该输入字符继续读取新字符，直到读取到一个非空格字符或者是输入表达式结束
	 */
	void stripSpace() {
		while (lookahead == ' ') {
			printLocation(indexOfLookahead);
			System.out.println("词法错误，表达式中不应有空格，已忽略该空格");
			System.out.println("======================================================");
			indexOfLookahead++;
			if (indexOfLookahead == input.size()) {
				break;
			}
			lookahead = input.get(indexOfLookahead);
		}
	}

}