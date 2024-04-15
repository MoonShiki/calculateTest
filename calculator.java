package test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;

public class calculator {
    public static BigDecimal calculate(String expression) {
        Stack<BigDecimal> nums = new Stack<>();
        Stack<Character> ops = new Stack<>();
        //遍历字符串
        for (int index = 0; index < expression.length(); index++) {
            char c = expression.charAt(index);
            //判断该字符是否属于数字字符
            if (Character.isDigit(c)) {
                int lastIndex = index;
                //获取未知位数数字最后一位下标
                while (lastIndex < expression.length() && (Character.isDigit(expression.charAt(lastIndex)) || '.' == expression.charAt(lastIndex))) {
                    lastIndex++;
                }
                //将数字字符入栈
                nums.push(new BigDecimal(expression.substring(index, lastIndex)));
                //坐标移动
                index = lastIndex - 1;
            } else {
                //遇到乘除的情况，将结果计算出来，最终符号栈除了栈顶元素只存储加减符号方便计算
                while (!ops.empty() && needCalculatePre(c, ops.peek())) {
                    nums.push(calculateByOps(ops.pop(), nums.pop(), nums.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.empty()) {
            //将计算结果入栈，直到存储符号的栈空
            nums.push(calculateByOps(ops.pop(), nums.pop(), nums.pop()));
        }

        return nums.pop();
    }

    //判断连续两个计算符是否为乘除和加减的情况
    private static boolean needCalculatePre(char op1, char op2) {
        return (op2 == '*' || op2 == '/') && (op1 == '+' || op1 == '-');
    }

    //计算时注意计算顺序，两个出栈参数后出栈的放在前面
    private static BigDecimal calculateByOps(char op, BigDecimal b, BigDecimal a) {
        switch (op) {
            case '+':
                return a.add(b);
            case '-':
                return a.subtract(b);
            case '*':
                return a.multiply(b).setScale(4, BigDecimal.ROUND_HALF_UP);
            case '/':
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new UnsupportedOperationException("Cannot divide, because parameter is zero");
                }
                //保留四位小数 四舍五入
                return a.divide(b, 4, RoundingMode.HALF_UP);
            default:
                throw new UnsupportedOperationException("Unknown operator in your input" + op);
        }
    }
}

