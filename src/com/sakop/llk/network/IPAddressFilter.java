package com.sakop.llk.network;

import com.sakop.llk.R;
import java.util.StringTokenizer;

public class IPAddressFilter {

	public static boolean check(String source) {
		StringTokenizer tokenizer = new StringTokenizer(source.toString(), ".");
		try {
			if (tokenizer.countTokens() != 4)
				return false;
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				int number = Integer.parseInt(token);
				if (number < 0 && number > 255)
					return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
