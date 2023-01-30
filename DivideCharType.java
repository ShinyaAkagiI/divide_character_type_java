import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;


public class DivideCharType {
  public static void main(String[] args){
    Divide d = new Divide();
    List<List<String>> data = null;

    if (args.length == 1){
      data = d.divideText(args[0], true);
    }
    else if (args.length == 2){
      data = d.divideText(args[0], Boolean.parseBoolean(args[1]));
    }
    else {
      System.out.println("引数1に解析するテキストを、引数２にBoolean（文中の接続記号を前に結合するかどうか）を入力してください");
      System.exit(1);
    }

    System.out.println(data);
  }
}


class Divide {
  public List<List<String>> divideText(String text, boolean concat_conj_in_ja){
    Pattern pattern_hiragana = Pattern.compile("[ぁ-ゖー～]");        // 平仮名の正規表現
    Pattern pattern_katakana = Pattern.compile("[ァ-ヴｦ-ｯｱ-ﾝー～]");  // カタカナの正規表現
    Pattern pattern_cjk = Pattern.compile("[一-龠々]");               // 漢字の正規表現
    Pattern pattern_alpha = Pattern.compile("[A-Za-zＡ-Ｚａ-ｚ]");    // アルファベットの正規表現
    Pattern pattern_digit = Pattern.compile("[0-9０-９]");            // 数字の正規表現

    List<String> list_all = new ArrayList<>();       // 字種分割語リスト
    List<String> list_hiragana = new ArrayList<>();  // 平仮名の分割語リスト
    List<String> list_katakana = new ArrayList<>();  // カタカナの分割語リスト
    List<String> list_cjk = new ArrayList<>();       // 漢字の分割語リスト
    List<String> list_alpha = new ArrayList<>();     // アルファベットの分割語リスト
    List<String> list_digit = new ArrayList<>();     // 数字の分割語リスト
    List<String> list_other = new ArrayList<>();     // その他記号などの分割語リスト
    
    String tmp_char_class = null;  // 一つ前の文字種
    String conj = null;            // 接続記号
    List<String> conjlist = Arrays.asList(".", "&", "．", "＆");       // 接続記号の一覧
    List<String> end_period = Arrays.asList("e.g", "u.s", "U.S", "u.s.a");  // 後置ピリオドが付く用語

    boolean firststep = true;

    // 字種分割
    String[] words = text.split("");
    for (String s : words) {
      // matcherの定義
      Matcher matcher_hiragana = pattern_hiragana.matcher(s);
      Matcher matcher_katakana = pattern_katakana.matcher(s);
      Matcher matcher_cjk = pattern_cjk.matcher(s);
      Matcher matcher_alpha = pattern_alpha.matcher(s);
      Matcher matcher_digit = pattern_digit.matcher(s);

      if (firststep){
        // 平仮名の場合
        if (matcher_hiragana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_hiragana.add(s);         // 平仮名リストへの追加
          tmp_char_class = "hiragana";  // 現在の文字種
        }
        // カタカナの場合
        else if (matcher_katakana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_katakana.add(s);         // カタカナリストへの追加
          tmp_char_class = "katakana";  // 現在の文字種
        }
        // 漢字の場合
        else if (matcher_cjk.matches()){
          list_all.add(s);         // 字種分割リストへの追加
          list_cjk.add(s);         // 漢字リストへの追加
          tmp_char_class = "cjk";  // 現在の文字種
        }
        // アルファベットの場合
        else if (matcher_alpha.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_alpha.add(s);         // アルファベットリストへの追加
          tmp_char_class = "alpha";  // 現在の文字種
        }
        // 数字の場合
        else if (matcher_digit.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_digit.add(s);         // 数字リストへの追加
          tmp_char_class = "digit";  // 現在の文字種
        }
        // その他記号などの場合
        else {
          list_all.add(s);        // 字種分割リストへの追加
          list_other.add(s);      // その他リストへの追加
          tmp_char_class = null;  // 現在の文字種
        }

        firststep = false;
        continue;
      }

      // リストの末尾の要素数
      int end_of_list_all = list_all.size()-1;
      int end_of_list_hiragana = list_hiragana.size()-1;
      int end_of_list_katakana = list_katakana.size()-1;
      int end_of_list_cjk = list_cjk.size()-1;
      int end_of_list_alpha = list_alpha.size()-1;
      int end_of_list_digit = list_digit.size()-1;
      int end_of_list_other = list_other.size()-1;

      // 一つ前が接続記号の場合
      if (conj != null){
        // 平仮名の場合
        if (matcher_hiragana.matches()){
          // アルファベットや数字の後にある接続記号を前の単語に結合する場合
          if (concat_conj_in_ja){
            // 接続記号の結合
            list_all.remove(end_of_list_all);
            end_of_list_all = list_all.size()-1;
            list_other.remove(end_of_list_other);
            list_all.set(end_of_list_all, list_all.get(end_of_list_all) + conj);
            if (tmp_char_class == "alpha"){
              list_alpha.set(end_of_list_alpha, list_alpha.get(end_of_list_alpha) + conj);
            }
            else if (tmp_char_class == "digit"){
              list_digit.set(end_of_list_digit, list_digit.get(end_of_list_digit) + conj);
            }
          }
          else {
            // 接続記号の位置調整
            list_other.remove(end_of_list_other);
            list_other.add(conj);          
          }
          list_all.add(s);              // 字種分割リストへの追加
          list_hiragana.add(s);         // 平仮名リストへの追加
          tmp_char_class = "hiragana";  // 現在の文字種
          conj = null;                  // conjの初期化
        }
        // カタカナの場合
        else if (matcher_katakana.matches()){
          // アルファベットや数字の後にある接続記号を前の単語に結合する場合
          if (concat_conj_in_ja){
            // 接続記号の結合
            list_all.remove(end_of_list_all);
            end_of_list_all = list_all.size()-1;
            list_other.remove(end_of_list_other);
            list_all.set(end_of_list_all, list_all.get(end_of_list_all) + conj);
            if (tmp_char_class == "alpha"){
              list_alpha.set(end_of_list_alpha, list_alpha.get(end_of_list_alpha) + conj);
            }
            else if (tmp_char_class == "digit"){
              list_digit.set(end_of_list_digit, list_digit.get(end_of_list_digit) + conj);
            }
          }
          else {
            // 接続記号の位置調整
            list_other.remove(end_of_list_other);
            list_other.add(conj);          
          }
          list_all.add(s);              // 字種分割リストへの追加
          list_katakana.add(s);         // 平仮名リストへの追加
          tmp_char_class = "katakana";  // 現在の文字種
          conj = null;                  // conjの初期化
        }
        // 漢字の場合
        else if (matcher_cjk.matches()){
          // アルファベットや数字の後にある接続記号を前の単語に結合する場合
          if (concat_conj_in_ja){
            // 接続記号の結合
            list_all.remove(end_of_list_all);
            end_of_list_all = list_all.size()-1;
            list_other.remove(end_of_list_other);
            list_all.set(end_of_list_all, list_all.get(end_of_list_all) + conj);
            if (tmp_char_class == "alpha"){
              list_alpha.set(end_of_list_alpha, list_alpha.get(end_of_list_alpha) + conj);
            }
            else if (tmp_char_class == "digit"){
              list_digit.set(end_of_list_digit, list_digit.get(end_of_list_digit) + conj);
            }
          }
          else {
            // 接続記号の位置調整
            list_other.remove(end_of_list_other);
            list_other.add(conj);          
          }
          list_all.add(s);         // 字種分割リストへの追加
          list_cjk.add(s);         // 平仮名リストへの追加
          tmp_char_class = "cjk";  // 現在の文字種
          conj = null;             // conjの初期化
        }
        // アルファベットの場合
        else if (matcher_alpha.matches()){
          // 二つ前がアルファベットの場合
          if (tmp_char_class == "alpha"){
            // 接続記号の中間結合
            list_all.remove(end_of_list_all);
            end_of_list_all = list_all.size()-1;
            list_other.remove(end_of_list_other);
            list_all.set(end_of_list_all, list_all.get(end_of_list_all) + conj + s);
            list_alpha.set(end_of_list_alpha, list_alpha.get(end_of_list_alpha) + conj + s);
          }
          else {
            list_all.add(s);    // 字種分割リストへの追加
            list_alpha.add(s);  // アルファベットリストへの追加
          }
          tmp_char_class = "alpha";  // 現在の文字種
          conj = null;               // conjの初期化
        }
        // 数字の場合
        else if (matcher_digit.matches()){
          // 二つ前が数字の場合
          if (tmp_char_class == "digit"){
            // 接続記号の中間結合
            list_all.remove(end_of_list_all);
            end_of_list_all = list_all.size()-1;
            list_other.remove(end_of_list_other);
            list_all.set(end_of_list_all, list_all.get(end_of_list_all) + conj + s);
            list_digit.set(end_of_list_digit, list_digit.get(end_of_list_digit) + conj + s);
          }
          else {
            list_all.add(s);    // 字種分割リストへの追加
            list_digit.add(s);  // アルファベットリストへの追加
          }
          tmp_char_class = "digit";  // 現在の文字種
          conj = null;               // conjの初期化
        }
        // その他記号などの場合
        else {
          // 後置ピリオドの場合
          if (conj == "." && end_period.contains(list_all.get(end_of_list_all-1))){
            // 接続記号の結合
            list_all.remove(end_of_list_all);
            end_of_list_all = list_all.size()-1;
            list_other.remove(end_of_list_other);
            list_all.set(end_of_list_all, list_all.get(end_of_list_all) + conj);
            if (tmp_char_class == "alpha"){
              list_alpha.set(end_of_list_alpha, list_alpha.get(end_of_list_alpha) + conj);
            }
            else if (tmp_char_class == "digit"){
              list_digit.set(end_of_list_digit, list_digit.get(end_of_list_digit) + conj);
            }
            list_all.add(s);    // 字種分割リストへの追加
            list_other.add(s);  // その他リストへの追加
          }
          else {
            list_all.set(end_of_list_all, list_all.get(end_of_list_all) + s);          // 字種分割リストへの結合
            list_other.set(end_of_list_other, list_other.get(end_of_list_other) + s);  // その他リストへの結合
          }
          tmp_char_class = null;  // 現在の文字種
          conj = null;            // conjの初期化
        }
      }
      // 一つ前が平仮名の場合
      else if (tmp_char_class == "hiragana"){
        // 平仮名の場合
        if (matcher_hiragana.matches()){
          list_all.set(end_of_list_all, list_all.get(end_of_list_all) + s);                       // 字種分割リストへの結合
          list_hiragana.set(end_of_list_hiragana, list_hiragana.get(end_of_list_hiragana) + s);  // 平仮名リストへの結合
        }
        // カタカナの場合
        else if (matcher_katakana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_katakana.add(s);         // カタカナリストへの追加
          tmp_char_class = "katakana";  // 現在の文字種
        }
        // 漢字の場合
        else if (matcher_cjk.matches()){
          list_all.add(s);         // 字種分割リストへの追加
          list_cjk.add(s);         // 漢字リストへの追加
          tmp_char_class = "cjk";  // 現在の文字種
        }
        // アルファベットの場合
        else if (matcher_alpha.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_alpha.add(s);         // アルファベットリストへの追加
          tmp_char_class = "alpha";  // 現在の文字種
        }
        // 数字の場合
        else if (matcher_digit.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_digit.add(s);         // 数字リストへの追加
          tmp_char_class = "digit";  // 現在の文字種
        }
        // その他記号などの場合
        else {
          list_all.add(s);        // 字種分割リストへの追加
          list_other.add(s);      // その他リストへの追加
          tmp_char_class = null;  // 現在の文字種
        }
      }
      // 一つ前がカタカナの場合
      else if (tmp_char_class == "katakana"){
        // カタカナの場合
        if (matcher_katakana.matches()){
          list_all.set(end_of_list_all, list_all.get(end_of_list_all) + s);                      // 字種分割リストへの結合
          list_katakana.set(end_of_list_katakana, list_katakana.get(end_of_list_katakana) + s);  // カタカナリストへの結合
        }
        // 平仮名の場合
        else if (matcher_hiragana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_hiragana.add(s);         // 平仮名リストへの追加
          tmp_char_class = "hiragana";  // 現在の文字種
        }
        // 漢字の場合
        else if (matcher_cjk.matches()){
          list_all.add(s);         // 字種分割リストへの追加
          list_cjk.add(s);         // 漢字リストへの追加
          tmp_char_class = "cjk";  // 現在の文字種
        }
        // アルファベットの場合
        else if (matcher_alpha.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_alpha.add(s);         // アルファベットリストへの追加
          tmp_char_class = "alpha";  // 現在の文字種
        }
        // 数字の場合
        else if (matcher_digit.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_digit.add(s);         // 数字リストへの追加
          tmp_char_class = "digit";  // 現在の文字種
        }
        // その他記号などの場合
        else {
          list_all.add(s);        // 字種分割リストへの追加
          list_other.add(s);      // その他リストへの追加
          tmp_char_class = null;  // 現在の文字種
        }
      }
      // 一つ前が漢字の場合
      else if (tmp_char_class == "cjk"){
        // 漢字の場合
        if (matcher_cjk.matches()){
          list_all.set(end_of_list_all, list_all.get(end_of_list_all) + s);  // 字種分割リストへの結合
          list_cjk.set(end_of_list_cjk, list_cjk.get(end_of_list_cjk) + s);  // 漢字リストへの結合
        }
        // 平仮名の場合
        else if (matcher_hiragana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_hiragana.add(s);         // 平仮名リストへの追加
          tmp_char_class = "hiragana";  // 現在の文字種
        }
        // カタカナの場合
        else if (matcher_katakana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_katakana.add(s);         // カタカナリストへの追加
          tmp_char_class = "katakana";  // 現在の文字種
        }
        // アルファベットの場合
        else if (matcher_alpha.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_alpha.add(s);         // アルファベットリストへの追加
          tmp_char_class = "alpha";  // 現在の文字種
        }
        // 数字の場合
        else if (matcher_digit.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_digit.add(s);         // 数字リストへの追加
          tmp_char_class = "digit";  // 現在の文字種
        }
        // その他記号などの場合
        else {
          list_all.add(s);        // 字種分割リストへの追加
          list_other.add(s);      // その他リストへの追加
          tmp_char_class = null;  // 現在の文字種
        }
      }
      // 一つ前がアルファベットの場合
      else if (tmp_char_class == "alpha"){
        // アルファベットの場合
        if (matcher_alpha.matches()){
          list_all.set(end_of_list_all, list_all.get(end_of_list_all) + s);          // 字種分割リストへの結合
          list_alpha.set(end_of_list_alpha, list_alpha.get(end_of_list_alpha) + s);  // アルファベットリストへの結合
        }
        // 平仮名の場合
        else if (matcher_hiragana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_hiragana.add(s);         // 平仮名リストへの追加
          tmp_char_class = "hiragana";  // 現在の文字種
        }
        // カタカナの場合
        else if (matcher_katakana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_katakana.add(s);         // カタカナリストへの追加
          tmp_char_class = "katakana";  // 現在の文字種
        }
        // 漢字の場合
        else if (matcher_cjk.matches()){
          list_all.add(s);         // 字種分割リストへの追加
          list_cjk.add(s);         // 漢字リストへの追加
          tmp_char_class = "cjk";  // 現在の文字種
        }
        // 数字の場合
        else if (matcher_digit.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_digit.add(s);         // 数字リストへの追加
          tmp_char_class = "digit";  // 現在の文字種
        }
        // 接続記号の場合
        else if (conjlist.contains(s)){
          conj = s;           // 接続記号の登録
          list_all.add(s);    // 字種分割リストへの追加
          list_other.add(s);  // その他リストへの追加
        }
        // その他記号などの場合
        else {
          list_all.add(s);        // 字種分割リストへの追加
          list_other.add(s);      // その他リストへの追加
          tmp_char_class = null;  // 現在の文字種
        }
      }
      // 一つ前が数字の場合
      else if (tmp_char_class == "digit"){
        // 数字の場合
        if (matcher_digit.matches()){
          list_all.set(end_of_list_all, list_all.get(end_of_list_all) + s);          // 字種分割リストへの結合
          list_digit.set(end_of_list_digit, list_digit.get(end_of_list_digit) + s);  // 数字リストへの結合
        }
        // 平仮名の場合
        else if (matcher_hiragana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_hiragana.add(s);         // 平仮名リストへの追加
          tmp_char_class = "hiragana";  // 現在の文字種
        }
        // カタカナの場合
        else if (matcher_katakana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_katakana.add(s);         // カタカナリストへの追加
          tmp_char_class = "katakana";  // 現在の文字種
        }
        // 漢字の場合
        else if (matcher_cjk.matches()){
          list_all.add(s);         // 字種分割リストへの追加
          list_cjk.add(s);         // 漢字リストへの追加
          tmp_char_class = "cjk";  // 現在の文字種
        }
        // アルファベットの場合
        else if (matcher_alpha.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_alpha.add(s);         // アルファベットリストへの追加
          tmp_char_class = "alpha";  // 現在の文字種
        }
        // 接続記号の場合
        else if (conjlist.contains(s)){
          conj = s;           // 接続記号の登録
          list_all.add(s);    // 字種分割リストへの追加
          list_other.add(s);  // その他リストへの追加
        }
        // その他記号などの場合
        else {
          list_all.add(s);        // 字種分割リストへの追加
          list_other.add(s);      // その他リストへの追加
          tmp_char_class = null;  // 現在の文字種
        }
      }
      // 一つ前がその他記号などの場合
      else {
        // 平仮名の場合
        if (matcher_hiragana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_hiragana.add(s);         // 平仮名リストへの追加
          tmp_char_class = "hiragana";  // 現在の文字種
        }
        // カタカナの場合
        else if (matcher_katakana.matches()){
          list_all.add(s);              // 字種分割リストへの追加
          list_katakana.add(s);         // カタカナリストへの追加
          tmp_char_class = "katakana";  // 現在の文字種
        }
        // 漢字の場合
        else if (matcher_cjk.matches()){
          list_all.add(s);         // 字種分割リストへの追加
          list_cjk.add(s);         // 漢字リストへの追加
          tmp_char_class = "cjk";  // 現在の文字種
        }
        // アルファベットの場合
        else if (matcher_alpha.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_alpha.add(s);         // アルファベットリストへの追加
          tmp_char_class = "alpha";  // 現在の文字種
        }
        // 数字の場合
        else if (matcher_digit.matches()){
          list_all.add(s);           // 字種分割リストへの追加
          list_digit.add(s);         // 数字リストへの追加
          tmp_char_class = "digit";  // 現在の文字種
        }
        // その他記号などの場合
        else {
          list_all.set(end_of_list_all, list_all.get(end_of_list_all) + s);          // 字種分割リストへの結合
          list_other.set(end_of_list_other, list_other.get(end_of_list_other) + s);  // その他リストへの結合
        }
      }
    }

    // 戻り値（字種分割語リスト、平仮名リスト、カタカナリスト、漢字リスト、アルファベットリスト、数字リスト、その他リスト）
    List<List<String>> data = Arrays.asList(list_all, list_hiragana, list_katakana, list_cjk, list_alpha, list_digit, list_other);
    return data;
  }
}
