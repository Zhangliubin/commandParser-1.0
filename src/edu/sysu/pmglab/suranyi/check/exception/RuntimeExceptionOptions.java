package edu.sysu.pmglab.suranyi.check.exception;

/**
 * @author suranyi
 * @description 普通异常参数
 */
public enum RuntimeExceptionOptions implements IRuntimeExceptionOptions {
    /**
     * 普通异常类型
     */
    AssertionError,
    ArrayIndexOutOfBoundsException,
    ArgumentOutOfRangeException,
    EmptyArrayException,
    EmptyCollectionException,
    EmptyMapException,
    EmptyStringException,
    NullPointerException,
    UnsupportedOperationException,
    NegativeValueException,
    ClassCastException;

    @Override
    public void throwException(String reason) {
        switch (this) {
            case ArrayIndexOutOfBoundsException:
                throw new ArrayIndexOutOfBoundsException(reason);
            case ArgumentOutOfRangeException:
                throw new ArgumentOutOfRangeException(reason);
            case EmptyArrayException:
                throw new EmptyArrayException(reason);
            case EmptyCollectionException:
                throw new EmptyCollectionException(reason);
            case EmptyMapException:
                throw new EmptyMapException(reason);
            case EmptyStringException:
                throw new EmptyStringException(reason);
            case NullPointerException:
                throw new NullPointerException(reason);
            case UnsupportedOperationException:
                throw new UnsupportedOperationException(reason);
            case NegativeValueException:
                throw new NegativeValueException(reason);
            case ClassCastException:
                throw new ClassCastException(reason);
            default:
                throw new AssertionError(reason);
        }
    }
}