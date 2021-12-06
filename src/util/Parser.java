package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Класс, предназначенный для конвертирования различных строк
 */
public class Parser {
    // Метод для конвертирования выражения в обратную польскую запись
    // 1. Входная строка проверяется на корректность данных и раскладывается на лексемы, у каждой из которых различный приоритет
    // 2. Если входные данные некорректны (проверяется то, что используются только числа, знаки операций, точка и скобки), выводим сообщение об этом
    // 3. Создаем стек и результирующую строку
    // 4. Проходим по списку лексем
    //      - если лексема - это число (приоритет 0) - записываем в результирующую строку
    //      - если лексема - это открывающая скобка (приоритет 1) - кладем наверх стека
    //      - если лексема - это оператор (приоритет > 1) - циклом проходим по стеку, пока он не пуст:
    //          1. если приоритет элемента на вершине стека >= приоритета текущего элемента, то переносим элемент из стека в результирующую строку,
    //          иначе останавливаем цикл
    //          2. кладем в стек текущий элемент
    //      - если лексема - это закрывающая скобка (приоритет -1), то переносим элементы из стека в результирующую строку до тех пор,
    //      пока не встретим открывающую скобку
    //  5. Пока в стеке есть элементы, заносим их в результирующую строку
    static String parseToRPN(String expression) throws InvalidDataException {

        // Проверка на корректность введенных данных
        if (!Validator.isValidForRPN(expression)) throw new InvalidDataException("Некорректно введенные данные");

        StringBuilder output = new StringBuilder();
        Stack<String> stack = new Stack<>();
        List<String> tokens = parseToTokens(expression);
        int priority;

        for (String token : tokens) {
            priority = getPriority(token);
            if (priority == 0) output.append(token).append(" ");
            if (priority == 1) stack.push(token);

            if (priority > 1) {
                while (!stack.empty()) {
                    if (getPriority(stack.peek()) >= priority) output.append(stack.pop()).append(" ");
                    else break;
                }
                stack.push(token);
            }

            if (priority == -1) {
                while (getPriority(stack.peek()) != 1) output.append(stack.pop()).append(" ");
                stack.pop();
            }
        }
        while (!stack.empty()) output.append(stack.pop()).append(" ");
        return output.toString();
    }

    // Метод для конвертирования выражения в список лексем (пробелы не учитываются)
    private static List<String> parseToTokens(String expression) {

        String expressionWithoutSpaces = expression.replace(" ", "");
        List<String> list = new ArrayList<>();

        for (int i = 0; i < expressionWithoutSpaces.length(); i++) {
            StringBuilder tmp = new StringBuilder(String.valueOf(expressionWithoutSpaces.charAt(i)));

            // Если элемент не является числом, то добавляем его в лист
            if (!Character.isDigit(expressionWithoutSpaces.charAt(i))) {
                // Также делаем проверку на унарный минус
                // В случае обнаружения унарного минуса поменяем его отображения на "±"
                if ((i == 0 && tmp.toString().equals("-")) ||
                        (tmp.toString().equals("-") && expressionWithoutSpaces.charAt(i - 1) == '(')) {
                    tmp = new StringBuilder("±");
                }

                // Также сделаем дополнительную проверку на то, что перед открывающей скобкой знак "*" может не присутствовать,
                // а именно в конкретной задаче между множителем и скобкой, либо между двумя скобками (закрывающей и открывающей)
                if (tmp.toString().equals("(") && i > 0 &&
                        (Character.isDigit(expressionWithoutSpaces.charAt(i - 1)) || expressionWithoutSpaces.charAt(i - 1) == ')')) {
                    list.add("*");
                }

                // Иначе добавляем число в лист полностью с учетом разрядности и плавающей точки
            } else {
                while (i < expressionWithoutSpaces.length() - 1 &&
                        (expressionWithoutSpaces.charAt(i + 1) == '.' || Character.isDigit(expressionWithoutSpaces.charAt(i + 1)))) {
                    tmp.append(expressionWithoutSpaces.charAt(i + 1));
                    i++;
                }
            }
            list.add(tmp.toString());
        }
        return list;
    }

    // Установка приоритетов различных лексем
    static int getPriority(String symbol) {
        if (symbol.equals("%")) return 6;
        if (symbol.equals("^")) return 5;
        if (symbol.equals("±")) return 4;
        if ((symbol.equals("*")) || (symbol.equals("/"))) return 3;
        if ((symbol.equals("+")) || (symbol.equals("-"))) return 2;
        if (symbol.equals("(")) return 1;
        if (symbol.equals(")")) return -1;
        return 0;
    }

    // Дополнительный метод преобразования строки в формате польской записи к списку лексем
    static List<String> parseToRPNTokens(String expressionToRPN) {
        List<String> tokensRPN = new ArrayList<>();
        StringBuilder num = new StringBuilder();

        for (int i = 0; i < expressionToRPN.length(); i++) {
            if (expressionToRPN.charAt(i) == ' ') continue;

            if (getPriority(String.valueOf(expressionToRPN.charAt(i))) == 0) {
                while (expressionToRPN.charAt(i) != ' ' && getPriority(String.valueOf(expressionToRPN.charAt(i))) == 0) {
                    num.append(expressionToRPN.charAt(i++));
                    if (i == expressionToRPN.length()) break;
                }
                tokensRPN.add(num.toString());
                num = new StringBuilder();
                continue;
            }
            tokensRPN.add(String.valueOf(expressionToRPN.charAt(i)));
        }
        return tokensRPN;
    }



}
