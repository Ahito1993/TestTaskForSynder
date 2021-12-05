package util;


/**
 * Класс, предназначенный для валидации данных
 */
public class Validator {

    // Метод, предназначенный для валидации данных, введенных с консоли
    // Проверка на то, что выражение введено корректно (пробелы не учитываются)
    public static boolean isValidForRPN(String expression) {
        // Удаление пробелов и проверка на пустые значения
        String expressionWithoutSpaces = expression.replace(" ", "");
        if (expressionWithoutSpaces.equals("")) {
            return false;
        }

        int numsCounter = 0;
        int leftBracketCounter = 0;
        int rightBracketCounter = 0;
        char current;
        char next;
        for (int i = 0; i < expressionWithoutSpaces.length(); i++) {
            current = expressionWithoutSpaces.charAt(i);

            if (i != expressionWithoutSpaces.length()-1) next = expressionWithoutSpaces.charAt(i + 1);
            else next = 'a';

            // Проверка на то, что каждый символ является цифрой или валидным знаком
            if (!Validator.isDigitOrValidSymbol(current)) return false;

            // Проверка на то, что присутствует хотя бы одна цифра
            if (Character.isDigit(current)) numsCounter++;

            // Проверка на то, что первый символ может быть "(", "-" или цифра
            if (i == 0 && isValidFirstSymbolOrSymbolAfterOpenBracket(current)) return false;
            // Проверка на то, что последний символ может быть ")", "%" или цифра
            if (i == expressionWithoutSpaces.length() - 1 && !isValidLastSymbol(current)) return false;

            // Проверка на то, что число с плавающей точкой записано корректно с точки зрения обычного пользователя,
            // так как запись числа в виде "1." приравнивается к виду 1.0
            if (isDot(current)) {
                if (i == expressionWithoutSpaces.length() - 1 || !Character.isDigit(next) || !Character.isDigit(expressionWithoutSpaces.charAt(i-1))) {
                    return false;
                }
            }
            // Подсчет скобок для дальнейшей проверки
            if (current == '(') leftBracketCounter++;
            if (current == ')') rightBracketCounter++;

            // Проверка на то, что 2 подряд идущих символа могут идти друг за другом
            if (isArithmeticOperator(current) && isArithmeticOperator(next) && current != '%') return false;
            if (current == '%' && (next == '(' || next == '%' || isDot(next))) return false;
            if (current == '(' && isValidFirstSymbolOrSymbolAfterOpenBracket(next)) return false;
            if (current == ')' && (next == '%' || next == '.' || Character.isDigit(next))) return false;
        }

        if (leftBracketCounter != rightBracketCounter) return false;
        return numsCounter > 0;
    }

    // Проверка на то, что символ является цифрой или одним из следующего перечня:
    // "+", "-", "*", "/", "%", ".", "^", "(", ")"
    private static boolean isDigitOrValidSymbol(char c) {
        return Character.isDigit(c) || isArithmeticOperator(c) || isBracket(c) || isDot(c);
    }

    // Проверка на то, что символ является допустимым арифметическим оператором
    private static boolean isArithmeticOperator(char c) {
        switch (c) {
            case ('-'):
            case ('+'):
            case ('*'):
            case ('/'):
            case ('^'):
            case ('%'):
                return true;
            default:
                return false;
        }
    }
    // Проверка на то, что строка является числом
    public static boolean isNumeric(String element) {
        try {
            Double.parseDouble(element);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Проверка на то, что символ является открывающей или закрывающей скобкой
    private static boolean isBracket(char c) {
        return c == '(' || c == ')';
    }

    // Проверка на то, что символ является точкой
    private static boolean isDot(char c) {
        return c == '.';
    }

    private static boolean isValidFirstSymbolOrSymbolAfterOpenBracket (char c) {
        return c != '(' && c != '-' && !Character.isDigit(c);
    }

    private static boolean isValidLastSymbol (char c) {
        return c == ')' || c == '%' || Character.isDigit(c);
    }
}
