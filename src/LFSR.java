public class LFSR {
    public static void main(String[] args) {
        //the register
        int register = 0;
        printRegister(register);
        byte[] initialSeed = {(byte) 0b10101010, (byte) 0b11110000, (byte) 0b00001111, (byte) 0b01010101};
        register = initRegister(initialSeed);
        printRegister(register);
        int leastBit = getLeastSignificantBit(register);
        register = shiftAndInsertTapBit(register, applyTapSequence(register));
        printRegister(register);
    }

    private static int shiftAndInsertTapBit(int register, byte tapBit) {
        register = register >>> 1;
        register = register | (tapBit << 31);
        return register;
    }

    private static byte getLeastSignificantBit(int register) {
        return (byte) (register & 1);
    }

    private static byte applyTapSequence(int register) {
        //TAP sequence x^31 + x^7 + x^5 + x^3 + x^2 + x + 1;
        byte[] index = {31, 7, 5, 3, 2, 1, 0};
        byte result = 0;
        for (int i = 0; i < index.length; i++) {
            byte bitValue = (byte) (((1 << index[i]) & register) >>> index[i]);
            result = (byte) (result ^ bitValue);
        }
        return result;
    }

    private static int initRegister(byte[] initialValues) {
        if(initialValues.length!=4){
            System.out.println("Wrong initial value. 4 bytes required");
            return 0;
        }
        int result=0;
        for(int i=3;i>=0;i--){
            result=result | ((initialValues[3-i] & 0xFF )<< (i*8));
            //0xFF is needed because when you convert a negative value to a integer negative value;
            //it counteracts what Java does when shifting negative numbers.
        }
        return result;
    }

    private static void printRegister(int register) {
        System.out.println("Register (binary): "+Integer.toBinaryString(register));
        System.out.println("Register (hex): "+Integer.toHexString(register).toUpperCase());
    }
}
