package app.netframe.encrypt;

import java.util.HashMap;

/***
 * 
 * @author smalllixin
 *			'A'=>'!',
            'B'=>'X',
            'C'=>'A',
            'D'=>'E',
            'E'=>'*',
            'F'=>'(',
            'G'=>'I',
            'H'=>'V',
            'I'=>'|',
            'J'=>')',
            'K'=>'-',
            'L'=>';',
            'M'=>'M',
            'N'=>'N',
            'O'=>'O',
            'P'=>'P',
            'Q'=>'Q',
            'R'=>'R',
            'S'=>'S',
            'T'=>'T',
            'U'=>'U',
            'V'=>'K',
            'W'=>'W',
            'X'=>'B',
            'Y'=>'.',
            'Z'=>'@'
 */
public class Base64 {
	char []codeOriginal = {
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U',
			'V','W','X','Y','Z',
	};
	char []codeTrans = {
			'!','X','A','E','*','(','I','V','|',')','-',';','M','N','O','P','Q','R','S','T','U','K','W','B','.','@'
	};
	
	int []codeEncrypt;
	
	HashMap<Character, Integer> decodeMap = new HashMap<Character, Integer>();
	
	private static Base64 _instance = null;
	
	public static Base64 Instance(){
		if (_instance == null){
			_instance = new Base64();
		}
		return _instance;
	}
	
	private Base64() {
		codeEncrypt = new int[codeOriginal.length];
		for (int i = 0; i < codeOriginal.length; i ++){
			//fill encode map
			char o = codeOriginal[i];
			char t = codeTrans[i];
			codeEncrypt[i] = t - o;
			
			//fill decode map
			decodeMap.put(t, i);
		}
	}
	
	public String encrypt(String input){

		
		return android.util.Base64.encodeToString(input.getBytes(), android.util.Base64.NO_WRAP);
	}
	
	public String decode(String src){
		char[] bs = src.toCharArray();
		char[] buf = new char[bs.length];
		for (int i = 0; i < bs.length; i ++){
			char b = bs[i];
			Integer pos = decodeMap.get(b);
			if (pos != null){
				buf[i] = codeOriginal[pos];
			}
			else {
				buf[i] = b;
			}
		}
		return new String(android.util.Base64.decode(new String(buf), android.util.Base64.DEFAULT));
	}
}
