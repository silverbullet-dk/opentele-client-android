package dk.silverbullet.telemed.device.continua.packet;

public class PrettyByteParser {
	public static byte[] parse(String prettyBytes) {
		String[] bytesAsString = prettyBytes.split("\\s+");
		byte[] result = new byte[bytesAsString.length];
		
		for (int i=0; i<bytesAsString.length; i++) {
			result[i] = parseByte(bytesAsString[i]);
		}
		return result;
	}

	private static byte parseByte(String string) {
		return (byte) Integer.parseInt(string, 16);
	}
}
