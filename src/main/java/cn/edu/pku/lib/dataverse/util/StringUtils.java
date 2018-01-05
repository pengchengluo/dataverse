/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.pku.lib.dataverse.util;

import java.util.List;
import java.util.Random;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 *
 * @author luopc
 */
public class StringUtils {
    
    public static boolean isChinese(String s){
        for(int i=0; i < s.length() ;i++){
            if(!isChinese(s.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
    
    public static String generateRandomString(int length) {
        StringBuilder str = new StringBuilder();
        String base = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        for (int i = 0; i < length; i++) {
            str.append(base.charAt(rnd.nextInt(base.length())));
        }
        return str.toString();
    }

    public static String listToString(List<String> list, String separator) {
        StringBuilder strBuilder = new StringBuilder();
        boolean notFirst = false;
        for (String element : list) {
            if (element == null || element.trim().length() == 0) {
                continue;
            }
            if (notFirst) {
                strBuilder.append(separator);
            } else {
                notFirst = true;
            }
            strBuilder.append(element);
        }
        return strBuilder.toString();
    }

    public static String jsonEscape(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
    
    public static String chineseToPinyinAndUpperCase(String hanzi){
        StringBuilder stringPinyin=new StringBuilder();
        HanyuPinyinOutputFormat pinformat=new HanyuPinyinOutputFormat();
        pinformat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        pinformat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        pinformat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        char sep = 127;
        char[] ar=hanzi.toCharArray();
        for(int i=0;i<ar.length;i++){
            try {
                String[] a = PinyinHelper.toHanyuPinyinStringArray(ar[i], pinformat);
                if(null!=a && a.length>0){
                    stringPinyin.append(a[0].charAt(0));
                    stringPinyin.append(sep);
                }else{
                    stringPinyin.append(ar[i]);
                }
            } catch(BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return stringPinyin.toString().toUpperCase();
    }
}
