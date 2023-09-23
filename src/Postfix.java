import java.io.*;
import java.util.*;

/**
 * ���࣬ͨ������Parser�࣬���ն��û������һ����׺���ʽת��Ϊ�ȼ۵ĺ�׺���ʽ�����
 */
public class Postfix {
	/**
	 * ����������������һ��Parser����
	 *
	 * @param args ������������������˳���δ�õ�
	 * @throws IOException the io exception
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Input an infix expression and output its postfix notation:");
		new Parser().expr();
		System.out.println("\nEnd of program.");
	}
}

/**
 * ���������׺���ʽ���з�����ת��Ϊ�ȼ۵ĺ�׺���ʽ�����
 */
class Parser {
	/**
	 * �洢�������׺���ʽ���б�
	 */
	static List<Character> input;
	/**
	 * �洢ת����ĺ�׺���ʽ���б�
	 */
	static List<Character> result;
	/**
	 * Ҫ��������һ���ַ�
	 */
	static int lookahead;
	/**
	 * Ҫ��������һ���ַ�������ı��ʽ�е�����
	 */
	static int indexOfLookahead;
	static int indexOfPre;

	/**
	 * �������������������ʼ����ر�������ȡ�û����벢���д洢
	 */
	public Parser() {
		/* ������ر��������г�ʼ�� */
		result = new ArrayList<>();
		input = new ArrayList<>();
		indexOfLookahead = 0;
		/* ��ȡ���� */
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
	 * ����term()��rest()����������׺���ʽ����ת���������ת����Ľ��
	 *
	 * @throws IOException the io exception
	 */
	void expr() throws IOException {
		term();
		rest();
		System.out.print("The output should be��");
		for (Character character : result) {
			System.out.print(character);
		}
	}

	/**
	 * ��⵱ǰλ���Ƿ�Ϊ�Ϸ��������+��-���� ���������match��term������ȡ��һλ����������������������б�����λ�����������Ϣ���Զ����Դ��ַ�����ȡ��һ���ַ�ֱ������+��-
	 *
	 * @throws IOException the io exception
	 */
	void rest() throws IOException {
		while (indexOfLookahead < input.size()) {
			if (Character.isDigit((char) lookahead)) {
				/* �������ͣ�������������ȱ������� */
				printLocation(indexOfLookahead);
				System.out.println("�﷨���󣬸���������ǰһ��������֮��ȱ����������Ѻ��Ը�������");
				System.out.println("======================================================");
				indexOfLookahead++;
				if (indexOfLookahead < input.size()) {
					lookahead = input.get(indexOfLookahead);
				}

			} else if (lookahead == ' ') {
				/* �ж������ַ��Ƿ�Ϊ�ո���������Ը������ַ���ֱ����ȡ��һ���ǿո��ַ�������������ʽ���� */
				stripSpace();
			} else if (lookahead != '+' && lookahead != '-') {
				/* �������ͣ�����Ƿ������ */
				printLocation(indexOfLookahead);
				System.out.println("�ʷ����󣬷Ƿ���������Ѻ��Ը��ַ�");
				System.out.println("======================================================");
				indexOfLookahead++;
				if (indexOfLookahead < input.size()) {
					lookahead = input.get(indexOfLookahead);
				}
			} else if (lookahead == '+') {
				/* ����+�������� */
				indexOfPre = indexOfLookahead;
				match('+');
				term();
				if (indexOfLookahead <= input.size())
					result.add('+');

			} else if (lookahead == '-') {
				/* ����-�������� */
				indexOfPre = indexOfLookahead;
				match('-');
				term();
				if (indexOfLookahead <= input.size())
					result.add('-');

			}
		}
	}

	/**
	 * ��⵱ǰ�������Ƿ�Ϊ���֡��������֣��򽫴����ּ��뵽����б��У����򱨴����Ը��ַ���������ȡ��һ���ַ�ֱ���������֡�
	 *
	 * @throws IOException the io exception
	 */
	void term() throws IOException {
		/* �ж������ַ��Ƿ�Ϊ���� */
		while (indexOfLookahead < input.size() && !Character.isDigit((char) lookahead)) {
			/* �ж������ַ��Ƿ�Ϊ�ո������������Ӧ�������д�������ֱ�ӱ������Ը����� */
			if (lookahead == ' ') {
				stripSpace();
			} else if (lookahead == '+' || lookahead == '-') {
				printLocation(indexOfLookahead);
				if (result.size() == 0) {
					System.out.println("�﷨����ȱ�������������Ѻ��Դ������ַ�");
				} else {
					System.out.println("�﷨����ǰһ�����ȱ�������������Ѻ��Դ������ַ�");
				}
				System.out.println("======================================================");
				indexOfLookahead++;
			} else {
				printLocation(indexOfLookahead);
				System.out.println("�ʷ����󣬷Ƿ���������Ѻ��Ը��ַ�");
				System.out.println("======================================================");
				indexOfLookahead++;
			}
			/* �ж������Ƿ�Խ�� */
			if (indexOfLookahead < input.size()) {
				lookahead = input.get(indexOfLookahead);
			}
		}
		/* ��δ����Խ�磬˵���Ѿ���������ַ����򽫴������ַ����뵽����б��� */
		if (indexOfLookahead < input.size()) {
			result.add((char) lookahead);
			match(lookahead);
		} else if (result.size() != 0) {
			printLocation(indexOfPre);
			indexOfLookahead++;
			System.out.println("�﷨���󣬸������ȱ�������������Ѻ��Ը�������ַ�");
			System.out.println("======================================================");
		}
	}

	/**
	 * �жϵ�ǰlookahead�봫����ַ��Ƿ�һ�¡���һ�����ȡ��һ���ַ����ж������Ƿ�Խ���Լ���һ���ַ��Ƿ�Ϊ�ո���Խ����ֹͣ������ȡ����Ϊ�ո�����õĺ�������б��������ָ���
	 *
	 * @param t ƥ���ַ�
	 * @throws IOException the io exception
	 */
	void match(int t) throws IOException {
		if (lookahead == t) {
			indexOfLookahead++;
			/* �ж������Ƿ�Խ�� */
			if (indexOfLookahead < input.size()) {
				lookahead = input.get(indexOfLookahead);
			}
			/* ���ú����ж���һ�������ַ��Ƿ�Ϊ�ո������������Ӧ���� */
			stripSpace();
		} else
			throw new Error("�﷨����");
	}

	/**
	 * ��ӡ������Ϣ����ʾ���������ַ��ڱ��ʽ�е�λ�á�
	 *
	 * @param index ��ǰ�����ַ��������б��е�����
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
	 * �ж������ַ��Ƿ�Ϊ�ո����ǿո�����Ը������ַ�������ȡ���ַ���ֱ����ȡ��һ���ǿո��ַ�������������ʽ����
	 */
	void stripSpace() {
		while (lookahead == ' ') {
			printLocation(indexOfLookahead);
			System.out.println("�ʷ����󣬱��ʽ�в�Ӧ�пո��Ѻ��Ըÿո�");
			System.out.println("======================================================");
			indexOfLookahead++;
			if (indexOfLookahead == input.size()) {
				break;
			}
			lookahead = input.get(indexOfLookahead);
		}
	}

}