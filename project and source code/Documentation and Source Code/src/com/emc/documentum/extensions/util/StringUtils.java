package com.emc.documentum.extensions.util;

import java.util.*;
import java.io.*;
import java.text.*;
import java.math.*;

public class StringUtils {

  /**
   * Rimuove le ripetizioni di un pattern dalla stringa specificata.
   * Un utilizzo potrebbe essere la rimozione di lettere doppie da una
   * stringa, come mostrato di seguito.<p>
   * <p>
   * <u>Esempio (rimozione di doppie)</u>
   * <pre>
   * String sSenzaRipetizioni = StringUtils.removePatternRipetition("Milazzo", "Z", true);
   * System.out.println(sSenzaRipetizioni);
   * </pre>
   * La stringa visualizzata e' <i>Milazo</i>.
   *
   * @param sText        stringa.
   * @param sPattern     pattern.
   * @param bUnsensitive <code>true</code> se l'analisi non &egrave;
   *                     case-sensitive, <code>false</code> altrimenti.
   *
   * @return la stringa senza ripetizioni.
   *
   * @since  JDK1.3
   */
  public static String removePatternRipetition(String sText, String sPattern, boolean bUnsensitive) {

    String sPatternCompare = (bUnsensitive) ? sPattern.toLowerCase() : sPattern;

    String[] aSplits = splitString(sText, sPatternCompare.length(), true, false);

    StringBuffer oNewText = new StringBuffer();

    boolean bAdded = false;

    for (int i = 0; i < aSplits.length; i ++) {

      String sSplit = aSplits[i];

      String sSplitCompare = (bUnsensitive) ? sSplit.toLowerCase() : sSplit;

      if (!sSplitCompare.equals(sPatternCompare)) {

        // Stringa diversa dal pattern

        oNewText.append(sSplit);

        bAdded = false;

      } else if (!bAdded) {

        // Pattern trovato per la prima volta

        oNewText.append(sSplit);

        bAdded = true;

      } else {

        // Pattern trovato altre volte

      }

    }

    return oNewText.toString();

  }

  /**
   * Sostituisce tutte le ripetizioni di un pattern nella stringa specificata
   * con un nuovo pattern.
   *
   * @param sText    stringa.
   * @param sPattern pattern da sostituire.
   * @param sPattern nuovo pattern.
   *
   * @return la nuova stringa.
   *
   * @since  JDK1.3
   */
  public static String replacePattern(String sText, String sPattern, String sNewPattern, boolean bUnsensitive) {

    StringBuffer oNewText = new StringBuffer();

    if (sText != null && !sText.equals("")) {

      String sTextCompare = (bUnsensitive) ? sText.toLowerCase() : sText;
      String sPatternCompare = (bUnsensitive) ? sPattern.toLowerCase() : sPattern;

      int iFromIndex = 0;
      int iIndexOf = sTextCompare.indexOf(sPatternCompare, iFromIndex);

      while (iIndexOf != -1) {

        oNewText.append(sText.substring(iFromIndex, iIndexOf));
        oNewText.append(sNewPattern);

        iFromIndex = iIndexOf + sPatternCompare.length();
        iIndexOf = sTextCompare.indexOf(sPatternCompare, iFromIndex);

      }

      oNewText.append(sText.substring(iFromIndex, sTextCompare.length()));

    }

    return oNewText.toString();

  }

  /**
   * Sostituisce tutte le ripetizioni dei pattern nella stringa specificata
   * con un nuovo pattern.
   *
   * @param sText     stringa.
   * @param aPatterns lista dei pattern da sostituire.
   * @param sPattern  nuovo pattern.
   *
   * @return la nuova stringa.
   *
   * @since  JDK1.3
   */
  public static String replacePatterns(String sText, String[] aPatterns, String sNewPattern, boolean bUnsensitive) {

    for (int i = 0; i < aPatterns.length; i ++) {

      sText = replacePattern(sText, aPatterns[i], sNewPattern, bUnsensitive);

    }

    return sText;

  }

  public static String wrapPattern(String sText, String sPattern, String sBefore, String sAfter, boolean bUnsensitive) {

    String sNewText;

    if (sText == null || sText.equals("")) {

      sNewText = "";

    } else if (sPattern == null || sPattern.equals("")) {

      sNewText = sText;

    } else {

      String sTextCompare = (bUnsensitive) ? sText.toLowerCase() : sText;
      String sPatternCompare = (bUnsensitive) ? sPattern.toLowerCase() : sPattern;

      String sPatternOriginal = null;

      StringBuffer oNewText = new StringBuffer();

      int iFromIndex = 0;
      int iIndexOf = sTextCompare.indexOf(sPatternCompare, iFromIndex);

      while (iIndexOf != -1) {

        if (sPatternOriginal == null) {

          sPatternOriginal = sText.substring(iIndexOf, iIndexOf + sPatternCompare.length());

        }

        oNewText.append(sText.substring(iFromIndex, iIndexOf));
        oNewText.append(sBefore);
        oNewText.append(sPatternOriginal);
        oNewText.append(sAfter);

        iFromIndex = iIndexOf + sPatternCompare.length();
        iIndexOf = sTextCompare.indexOf(sPatternCompare, iFromIndex);

      }

      oNewText.append(sText.substring(iFromIndex, sTextCompare.length()));

      sNewText = oNewText.toString();

    }

    return sNewText;

  }

  public static String wrapPatterns(String sText, String[] aPatterns, String sBefore, String sAfter, boolean bUnsensitive) {

    for (int i = 0; i < aPatterns.length; i ++) {

      sText = wrapPattern(sText, aPatterns[i], sBefore, sAfter, bUnsensitive);

    }

    return sText;

  }

  /**
   * Ridimensiona una stringa.
   *
   * @param sValue      stringa da ridimensionare.
   * @param iSize       nuova dimensione della stringa.
   * @param sFillerChar carattere di riempimento.
   * @param bAfter      se <code>true</code> inserisce il carattere di
   *                    riempimento in coda, se <code>false</code> in
   *                    testa.
   *
   * @return la nuova stringa.
   *
   * @since  JDK1.3
   */
  static public String formatString(String sValue, int iSize, String sFillerChar, boolean bAfter) {

    String sOutput;

    boolean bFillerChar = sFillerChar != null && !sFillerChar.equals("");

    if (sValue == null || sValue.equals("")) {

      StringBuffer oOutput = new StringBuffer();

      for (int i = 0; bFillerChar && i < iSize; i ++) {

        oOutput.append(sFillerChar);

      }

      sOutput = oOutput.toString();

    } else if (sValue.length() < iSize) {

      StringBuffer oOutput = new StringBuffer();

      for (int i = 0; bFillerChar && i < iSize - sValue.length(); i ++) {

        oOutput.append(sFillerChar);

      }

      if (bAfter) {

        sOutput = sValue + oOutput.toString();

      } else {

        sOutput = oOutput.toString() + sValue;

      }

    } else {

      sOutput = sValue.substring(0, iSize);

    }

    return sOutput;

  }

  /**
   * Divide una stringa in pi&ugrave; sotto-stringhe utilizzando il
   * separatore indicato come elemento delimitatore. Utile
   * per ottenere le parole che compongono una linea di testo.
   *
   * @param sLine          stringa da dividere.
   * @param sCharSeparator carattere o stringa da usare per
   *                       delimitare le sotto-stringhe.
   * @param bSort          se <code>true</code> ordina in modo crescente,
   *                       se <code>false</code> non effettua nessun
   *                       ordinamento.
   *
   * @return l'elenco delle sotto-stringhe oppure <code>null</code>
   *         se il delimitatore non &egrave; presente nella stringa.
   *
   * @throws Exception se c'è un errore durante la scansione e la
   *                   separazione.
   *
   * @since  JDK1.3
   */
  public static String[] splitString(String sLine, String sCharSeparator, boolean bSort) throws Exception {

    // Eliminare il tokenizer per eliminare il lancio dell'eccezione

    Vector<String> oTokens = new Vector<String>();

    if (sLine != null && !sLine.equals("")) {

      StringTokenizer oStringTokenizer = new StringTokenizer(sLine, sCharSeparator);

      while (oStringTokenizer.hasMoreTokens()) {

        oTokens.add((String) oStringTokenizer.nextToken().trim());

      }

    }

    return toStringArray(oTokens, bSort);

  }

  /**
   * Divide una stringa in sotto-stringhe di dimensione prefissata.
   *
   * @param sLine        stringa da dividere.
   * @param iSplitSize   dimensione delle sotto-stringhe.
   * @param bLeftToRight se <code>true</code> divide la stringa
   *                     partendo da sinistra verso destra, se
   *                     <code>false</code> da destra a sinistra.
   * @param bSort        se <code>true</code> ordina in modo crescente,
   *                     se <code>false</code> non effettua nessun
   *                     ordinamento.
   *
   * @return l'elenco delle sotto-stringhe oppure <code>null</code>
   *         se la dimensione &egrave; nulla o negativa.
   *         Se la dimensione specificata &egrave;
   *         maggiore della lunghezza della stringa ritorna la
   *         stringa stessa.
   *
   * @since  JDK1.3
   */
  static public String[] splitString(String sLine, int iSplitSize, boolean bLeftToRight, boolean bSort) {

    String[] aSplits;

    if (iSplitSize > 0) {

      Vector<String> oSplits = new Vector<String>();

      if (bLeftToRight) {

        int i = 0;

        while ((i + iSplitSize) < sLine.length()) {

          oSplits.add(sLine.substring(i, i + iSplitSize));

           i += iSplitSize;

        }

        oSplits.add(sLine.substring(i, sLine.length()));

      } else {

        int i = sLine.length();

        while ((i - iSplitSize) > 0) {

          oSplits.add(sLine.substring(i - iSplitSize, i));

           i -= iSplitSize;

        }

        oSplits.add(sLine.substring(0, i));

      }

      aSplits = toStringArray(oSplits, bSort);

    } else {

      aSplits = null;

    }

    return aSplits;

  }

  static public String[] splitString(String sValue, int iSize, int[] aSplitSizes) {

    String[] aStrings = new String[1];

    aStrings[0] = sValue;

    return splitStrings(aStrings, iSize, aSplitSizes);

  }

  static public String[] splitStrings(String[] aStrings, int iSize, int[] aSplitSizes) {

    System.out.println("splitStrings - In input: " + aStrings.length + " stringhe, dimensione " + iSize + ", e " + aSplitSizes.length + " dimensioni di split");

    String[] aSplits;

    int iSplitSize = 0;

    for (int i = 0; i < aSplitSizes.length; i ++) iSplitSize += aSplitSizes[i];

    if (iSplitSize > iSize) {

      System.out.println("ERROR: splitStrings - Le dimensioni di split eccedono la dimensione della stringa = " + iSize);

      aSplits = null;

    } else {

      StringBuffer oString = new StringBuffer();

      for (int i = 0; i < aStrings.length; i ++) oString.append(aStrings[i]);

      String sValue = formatString(oString.toString(), iSize, " ", true);

      System.out.println("splitStrings - Stringa di " + sValue.length() + " da splittare = [" + sValue + "]");

      aSplits = new String[aSplitSizes.length];

      int iStart = 0;
      int i;

      for (i = 0; i < aSplits.length; i ++) {

        int iStop = iStart + aSplitSizes[i];

        System.out.println("splitStrings - Split " + i + " della stringa [" + sValue + "] da " + iStart + " a " + iStop);

        aSplits[i] = sValue.substring(iStart, iStop);

        System.out.println("splitStrings - Risultato dello split " + i + " della stringa [" + sValue + "] da " + iStart + " a " + iStop + " = [" + aSplits[i] + "]");

        iStart = iStop;

      }

      System.out.println("splitStrings - Splittate " + i + " stringhe");

    }

    return aSplits;

  }

  static public String[] splitNumber(float fValue, int iNumDecimals) {

    System.out.println("splitNumber - Valore decimale da splittare = " + fValue);

    String sValue = new Float(fValue).toString();

    // Trova la parte intera e la parte decimale

    String sIntPart = "";
    String sDecPart = "";

    int iDecimalPos = sValue.indexOf(".", 0); // Il separatore originale e' il punto!

    if (iDecimalPos >= 0) {

      sIntPart = sValue.substring(0, iDecimalPos);

      String sDecPartTemp = sValue.substring(iDecimalPos + 1);

      StringBuffer oDecPart = new StringBuffer(sDecPartTemp);

      for (int i = 0; i < iNumDecimals - sDecPartTemp.length(); i ++) oDecPart.append("0");

      sDecPart = oDecPart.toString();

    } else {

      sIntPart = sValue;

      StringBuffer oDecPart = new StringBuffer();

      for (int i = 0; i < iNumDecimals; i ++) oDecPart.append("0");

      sDecPart = oDecPart.toString();

    }

    sIntPart = ((sIntPart.equals("")) ? "0" : sIntPart);

    System.out.println("splitNumber - Parte intera = " + sIntPart + ", Parte decimale a " + iNumDecimals + " cifre = " + sDecPart);

    // Valorizza la struttura di ritorno

    String[] aParts = new String[2];

    aParts[0] = sIntPart;
    aParts[1] = sDecPart;

    return aParts;

  }

  static public String formatNumber(float fValue, int iNumDecimals, String sDecimalSeparator, String sThousandSeparator, String sDefaultOutput) {

    System.out.println("formatNumber - Valore decimale da formattare = " + fValue);

    String sValue = new Float(fValue).toString();

    return formatNumber(sValue, iNumDecimals, sDecimalSeparator, sThousandSeparator, sDefaultOutput);

  }

  static public String formatNumber(String sValue, int iNumDecimals, String sDecimalSeparator, String sThousandSeparator, String sDefaultOutput) {

    String sResult;

    if (sValue == null || sValue.equals("")) {

      System.out.println("formatNumber - Valore decimale da formattare nullo, usa ritorna il default = " + sDefaultOutput);

      sResult = sDefaultOutput;

    } else {

      System.out.println("formatNumber - Valore decimale da formattare = " + sValue);

      // Trova la parte intera e la parte decimale

      String sIntPart = "";
      String sDecPart = "";

      int iDecimalPos = sValue.indexOf(".", 0); // Il separatore originale e' il punto!

      if (iDecimalPos >= 0) {

        sIntPart = sValue.substring(0, iDecimalPos);
        sDecPart = sValue.substring(iDecimalPos + 1);

      } else {

        sIntPart = sValue;
        sDecPart = "";

      }

      sIntPart = ((sIntPart.equals("")) ? "0" : sIntPart);

      System.out.println("formatNumber - Parte intera = " + sIntPart + ", Parte decimale = " + sDecPart);

      // Costruisce la nuova parte decimale

      String sNewDecPart = "";
      boolean bCarry;

      if (sDecPart.length() > iNumDecimals) {

        sNewDecPart = sDecPart.substring(0, iNumDecimals);

        System.out.println("formatNumber - Parte decimale troncata a " + iNumDecimals + " cifre = " + sNewDecPart);

        char cTruncatedDigit = sDecPart.charAt(iNumDecimals);

        bCarry = (sDecPart.charAt(iNumDecimals) > '5');

        System.out.println("formatNumber - Cifra troncata = " + cTruncatedDigit + ((bCarry) ? " (genera riporto)" : " (non genera riporto)"));

      } else if (sDecPart.length() < iNumDecimals) {

        System.out.println("formatNumber - Le cifre decimali sono meno di " + iNumDecimals);

        sNewDecPart = sDecPart;

        for (int i = 0; i < (iNumDecimals - sDecPart.length()); i++) {

          sNewDecPart = sNewDecPart.concat("0");

        }

        bCarry = false;

        System.out.println("formatNumber - Parte decimale fillata = " + sNewDecPart);

      } else {

        // Il numero e' gia' nel formato corretto

        System.out.println("formatNumber - Numero gia' nel formato corretto = " + sValue);

        sNewDecPart = sDecPart;
        bCarry = false;

      }

      if (sThousandSeparator != null && !sThousandSeparator.equals("")) {

        // Applica il separatore delle migliaia

        System.out.println("formatNumber - Applica il separatore delle migliaia sulla parte intera = " + sIntPart);

        sIntPart = thousandStringNumber(sIntPart, sThousandSeparator.charAt(0));

      }

      sResult = sIntPart + ((!sNewDecPart.equals("")) ? sDecimalSeparator : "") + sNewDecPart;

      System.out.println("formatNumber - Numero ottenuto = " + sResult);

      if (bCarry) {

        // Arrotonda per eccesso il numero troncato

        System.out.println("formatNumber - C'e' il riporto per cui il numero " + sResult + " viene arrotondato per eccesso");

        sResult = roundStringNumber(sResult, sDecimalSeparator.charAt(0), sThousandSeparator.charAt(0));

      }

      System.out.println("formatNumber - Numero formattato = " + sResult);

    }

    return sResult;

  }

  static private String thousandStringNumber(String sNumber, char cThousandSeparator) {

    System.out.println("thousandStringNumber - Numero su cui separare le migliaia = " + sNumber);

    StringBuffer oResult = new StringBuffer();

    String[] aThousands = splitString(sNumber, 3, false, false);

    for (int i = aThousands.length - 1; i >= 0; i --) {

      System.out.println("thousandStringNumber - Migliaia separata = " + aThousands[i]);

      if (i < aThousands.length - 1) oResult.append(cThousandSeparator);

      oResult.append(aThousands[i]);

    }

    System.out.println("thousandStringNumber - Numero con le migliaia separate = " + oResult.toString());

    return oResult.toString();

  }

  static private String roundStringNumber(String sNumber, char cDecimalSeparator, char cThousandSeparator) {

    System.out.println("roundStringNumber - Numero da arrotondare = " + sNumber);

    String sRounded = "";
    int iIndex = sNumber.length() - 1;
    boolean bCarry = true;

    while (bCarry && iIndex >= 0) {

      char cDigit = sNumber.charAt(iIndex);

      System.out.println("roundStringNumber - Cifra analizzata = " + cDigit);

      // Incrementa la cifra

      if (cDigit == '9') {

          cDigit = '0';
          bCarry = true;

      } else if (cDigit >= '0' && cDigit <= '8') {

          cDigit += 1; // Carettere successivo
          bCarry = false;

      } else if (cDigit == cDecimalSeparator) {

          // Il separatore decimale viene riportato tale e
          // quale ma non modifica la condizione sul riporto

      } else if (cDigit == cThousandSeparator) {

          // Il separatore delle migliaia viene riportato
          // tale e quale ma non modifica la condizione sul
          // riporto

      } else {

          bCarry = false;

      }

      System.out.println("roundStringNumber - Cifra analizzata = " + cDigit + ((bCarry) ? " (c'e' riporto)" : " (non c'e' riporto)"));

      sRounded = cDigit + sRounded;

      System.out.println("roundStringNumber - Parte decimale dopo l'innesto della cifra analizzata = " + sRounded);

      iIndex --;

    }

    String sResult = ((bCarry) ? "1" : sNumber.substring(0, iIndex + 1)) + sRounded;

    System.out.println("roundStringNumber - Numero arrotondato = " + sResult);

    return sResult;

  }

  static public BigDecimal toNumber(String sNumber, int iNumDecimals) {

    BigDecimal oNumber;

    try {

      System.out.println("toNumber - Inizio trasformazione del numero " + sNumber + " con numero di decimali " + Integer.toString(iNumDecimals));

      oNumber = new BigDecimal(sNumber).movePointLeft(iNumDecimals);

    } catch (Exception oException) {

      System.out.println("ERROR: toNumber - Errore nella trasformazione del numero " + sNumber + " con numero di decimali " + Integer.toString(iNumDecimals) + ", " + oException.getMessage());

      oNumber = new BigDecimal("0.00");

    }

    return oNumber;

  }

  static public int toInt(String sNumber) {

    int iNumber;

    try {

      System.out.println("toInt - Inizio trasformazione del numero " + sNumber);

      iNumber = Integer.parseInt(sNumber);

    } catch (Exception oException) {

      System.out.println("ERROR: toInt - Errore nella trasformazione del numero " + sNumber + ", " + oException.getMessage());

      iNumber = 0;

    }

    return iNumber;

  }

  /**
   * Converte un {@link java.util.Vector} di {@link java.lang.String}
   * in un array.
   *
   * @param oStringList lista di stringhe.
   * @param bSort       se <code>true</code> ordina in modo crescente,
   *                    se <code>false</code> non effettua nessun
   *                    ordinamento.
   *
   * @return l'array di stringhe.
   *
   * @since  JDK1.3
   */
  public static String[] toStringArray(List<String> oStringList, boolean bSort) {

    String[] aArray;

    if (oStringList.size() == 0) {

      aArray = null;

    } else {

      if (bSort) Collections.sort(oStringList);

      String[] aToCast = new String[oStringList.size()];

      aArray = (String[]) oStringList.toArray(aToCast);

    }

    return aArray;

  }

  /**
   * Applica un quick-sort per ordinate un array in modo
   * crescente. Gli oggetti contenuti nell'array devono
   * essere tutti dello stesso tipo e devono implementare
   * la classe {@link java.lang.Comparable}.
   *
   * @param aArray l'array da ordinare.
   *
   * @since  JDK1.3
   */
  public static void sortArray(Object[] aArray) {

    sortArray(aArray, 0, aArray.length - 1);

  }

  /**
   * Applica un quick-sort per ordinate una porzione di un array
   * in modo crescente. Gli oggetti contenuti nell'array devono
   * essere tutti dello stesso tipo e devono implementare
   * la classe {@link java.lang.Comparable}.
   *
   * @param aArray l'array da ordinare.
   * @param iStart posizione iniziale.
   * @param iEnd   posizione finale.
   *
   * @since  JDK1.3
   */
  public static void sortArray(Object[] aArray, int iStart, int iEnd) {

    if (iEnd > iStart) {

      int iPartition = partitionArray(aArray, iStart, iEnd);

      sortArray(aArray, iStart, iPartition - 1);
      sortArray(aArray, iPartition + 1, iEnd);

    }

  }

  /**
   * Determina la posizione di partizione di un array ordinato
   * con un quick-sort.
   *
   * @param aArray l'array.
   * @param iStart posizione iniziale.
   * @param iEnd   posizione finale.
   *
   * @return la posizione di partizione.
   *
   * @see sortArray(Object[])
   * @see sortArray(Object[], int, int)
   *
   * @since  JDK1.3
   */
private static int partitionArray(Object[] aArray, int iStart, int iEnd) {

    Comparable<Object> oPartitionElement = (Comparable) aArray[iEnd];

    int iLeft = iStart - 1;
    int iRight = iEnd;

    for (int i = 0; iLeft < iRight; i ++) {

      if (i > 0) swapArrayElement(aArray, iLeft, iRight);

      while (oPartitionElement.compareTo(aArray[++ iLeft]) >= 0 && iLeft != iEnd);

      while (oPartitionElement.compareTo(aArray[-- iRight]) < 0 && iRight != iStart);

    }

    swapArrayElement(aArray, iLeft, iEnd);

    return iLeft;

  }

  /**
   * Scambia due elementi di un array.
   *
   * @param aArray l'array.
   * @param i      posizione da scambiare.
   * @param j      posizione da scambiare.
   *
   * @return l'array dopo lo scambio.
   *
   * @see sortArray(Object[])
   * @see sortArray(Object[], int, int)
   *
   * @since  JDK1.3
   */
  public static void swapArrayElement(Object[] aArray, int i, int j) {

    Object oElement = aArray[i];

    aArray[i] = aArray[j];
    aArray[j] = oElement;

  }

  /**
   * Ritorna la data/ora nel formato specificato.
   *
   * @param  oDate   data/ora da formattare.
   * @param  sFormat stringa di formato (un valore fra
   *                 {@link #DATE_FORMAT},
   *                 {@link #DATE_COMPACT_FORMAT},
   *                 {@link #DATE_FILE_FORMAT},
   *                 {@link #TIME_FORMAT},
   *                 {@link #TIME_COMPACT_FORMAT},
   *                 {@link #TIME_EXTENDED_FORMAT},
   *                 {@link #TIME_FILE_FORMAT},
   *                 {@link #TIME_EXTENDED_FILE_FORMAT},
   *                 {@link #DATETIME_FORMAT}).
   *
   * @return la data/ora come stringa (una stringa di spazi nel
   *         caso di data/ora nulla).
   *
   * @since  JDK1.3
   */
  public static String formatDate(Date oDate, String sFormat) {

    String sDate;

    if (oDate == null) {

      StringBuffer oOutput = new StringBuffer();

      for (int i = 0; i < sFormat.length(); i ++) oOutput.append(" ");

      sDate = oOutput.toString();

    } else {

      SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(sFormat);
      sDate = oSimpleDateFormat.format(oDate);
      
      if (sFormat.equals(DATETIME_XML_FORMAT)) sDate = replacePattern(sDate, " ", "T", true);

    }

    return sDate;

  }

  /**
   * Ritorna la data/ora corrente nel formato specificato.
   *
   * @param  sFormat stringa di formato (un valore fra
   *                 {@link #DATE_FORMAT},
   *                 {@link #DATE_COMPACT_FORMAT},
   *                 {@link #DATE_FILE_FORMAT},
   *                 {@link #TIME_FORMAT},
   *                 {@link #TIME_COMPACT_FORMAT},
   *                 {@link #TIME_EXTENDED_FORMAT},
   *                 {@link #TIME_FILE_FORMAT},
   *                 {@link #TIME_EXTENDED_FILE_FORMAT},
   *                 {@link #DATETIME_FORMAT}).
   *
   * @return la data/ora come stringa.
   *
   * @since  JDK1.3
   */
  public static String formatCurrentDate(String sFormat) {

    SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(sFormat);
    
    String sDate = oSimpleDateFormat.format(new Date());
    
    if (sFormat.equals(DATETIME_XML_FORMAT)) sDate = replacePattern(sDate, " ", "T", true);

    return sDate;

  }

  /**
   * Ritorna la data/ora in formato come {@link #java.sql.Date}.
   *
   * @return la data/ora oppure <code>null</code> se fallisce la trasformazione.
   *
   * @since  JDK1.3
   */
  public static java.sql.Date toSqlDate(Date oDate) {

    return (oDate == null) ? null : new java.sql.Date(oDate.getTime());

  }

  /**
   * Ritorna la data/ora.
   *
   * @param sData la data/ora come stringa.
   * @param  sFormat stringa di formato (un valore fra
   *                 {@link #DATE_FORMAT},
   *                 {@link #DATE_COMPACT_FORMAT},
   *                 {@link #DATE_FILE_FORMAT},
   *                 {@link #DATETIME_FORMAT}).
   *
   * @return la data/ora oppure <code>null</code> se fallisce la trasformazione.
   *
   * @since  JDK1.3
   */
  public static Date toDate(String sDate, String sFormat) {

	if (sFormat.equals(DATETIME_XML_FORMAT)) sDate = replacePattern(sDate, "T", " ", true);
    
	Calendar oCalendar = toCalendar(sDate, sFormat);

    return (oCalendar == null) ? null : oCalendar.getTime();

  }

  /**
   * Ritorna la il timestamp riferito alla data/ora.
   *
   * @param sData la data/ora come stringa.
   * @param  sFormat stringa di formato (un valore fra
   *                 {@link #DATE_FORMAT},
   *                 {@link #DATE_COMPACT_FORMAT},
   *                 {@link #DATE_FILE_FORMAT},
   *                 {@link #DATETIME_FORMAT}).
   *
   * @return il timestamp oppure <code>null</code> se fallisce la trasformazione.
   *
   * @see    #DATE_FORMAT
   * @see    #DATE_COMPACT_FORMAT
   * @see    #DATE_FILE_FORMAT
   * @see    #DATETIME_FORMAT
   *
   * @since  JDK1.3
   */
  public static java.sql.Timestamp toTimestamp(String sDate, String sFormat) {

    Date oDate = toDate(sDate, sFormat);

    return (oDate == null) ? null : new java.sql.Timestamp(oDate.getTime());

  }

  /**
   * Ritorna il calendario riferito alla data/ora.
   *
   * @param sData la data/ora come stringa.
   * @param  sFormat stringa di formato (un valore fra
   *                 {@link #DATE_FORMAT},
   *                 {@link #DATE_COMPACT_FORMAT},
   *                 {@link #DATE_FILE_FORMAT},
   *                 {@link #DATETIME_FORMAT}).
   *
   * @return il calendario oppure <code>null</code> se fallisce la trasformazione.
   *
   * @since  JDK1.3
   */
  public static Calendar toCalendar(String sDate, String sFormat) {

    System.out.println("toCalendar - Split della data " + sDate + " nel formato " + sFormat);

    Calendar oCalendar = null;

    try {

      StringBuffer oDay = new StringBuffer();
      StringBuffer oMonth = new StringBuffer();
      StringBuffer oYear = new StringBuffer();

      StringBuffer oHours = new StringBuffer();
      StringBuffer oMinutes = new StringBuffer();
      StringBuffer oSeconds = new StringBuffer();

      StringBuffer oMillis = new StringBuffer();

      for (int i = 0; i < sFormat.length(); i ++) {

        char cFormat = sFormat.charAt(i);
        char cDate = sDate.charAt(i);

        if (cFormat == 'd') {

          oDay.append(cDate);

        } else if (cFormat == 'M') {

          oMonth.append(cDate);

        } else if (cFormat == 'y') {

          oYear.append(cDate);

        } else if (cFormat == 'H') {

          oHours.append(cDate);

        } else if (cFormat == 'm') {

          oMinutes.append(cDate);

        } else if (cFormat == 's') {

          oSeconds.append(cDate);

        } else if (cFormat == 'S') {

          oMillis.append(cDate);

        } else {

          // Caratteri separatori ignorati

        }

      }

      int iDay = Integer.parseInt(oDay.toString());
      int iMonth = Integer.parseInt(oMonth.toString());
      int iYear = Integer.parseInt(oYear.toString());

      if (oYear.toString().length() == 2) {

        iYear = ((iYear > 50) ? 1900 : 2000) + iYear;

      }

      int iHours = (oHours.length() == 0) ? 23 : Integer.parseInt(oHours.toString());
      int iMinutes = (oMinutes.length() == 0) ? 59 : Integer.parseInt(oMinutes.toString());
      int iSeconds = (oSeconds.length() == 0) ? 59 : Integer.parseInt(oSeconds.toString());

      int iMillis = (oMillis.length() == 0) ? 0 : Integer.parseInt(oMillis.toString());

      // Imposta la data

      oCalendar = new GregorianCalendar(iYear, iMonth - 1, iDay);

      // Imposta l'ora

      oCalendar.set(Calendar.HOUR_OF_DAY, iHours);
      oCalendar.set(Calendar.MINUTE, iMinutes);
      oCalendar.set(Calendar.SECOND, iSeconds);
      oCalendar.set(Calendar.MILLISECOND, iMillis);

      System.out.println("toCalendar - Data trasformata " + oCalendar.getTime());

    } catch (Exception oException) {

      System.out.println("ERROR: toCalendar - Errore nella trasformazione della data " + sDate + " dal formato " + sFormat + ", " + oException.getMessage());

      oCalendar = null;

    }

    return oCalendar;

  }

  /**
   * Calcola la differenza fra due data/ora in millisecondi, secondi,
   * ore, minuti, giorni.
   *
   * @param sData1 prima data/ora come stringa.
   * @param sData1 seconda data/ora come stringa.
   * @param sFormat stringa di formato delle due data/ora (un valore fra
   *                 {@link #DATE_FORMAT},
   *                 {@link #DATE_COMPACT_FORMAT},
   *                 {@link #DATE_FILE_FORMAT},
   *                 {@link #DATETIME_FORMAT}).
   * @param lBaseMillis coefficiente di normalizzazione (un valore fra
   *                 {@link #MILLISECONDS_ONE_SECOND},
   *                 {@link #MILLISECONDS_ONE_MINUTE},
   *                 {@link #MILLISECONDS_ONE_HOUR},
   *                 {@link #MILLISECONDS_ONE_DAY}).
   *
   * @return la differenza in valore assoluto.
   *
   * @since  JDK1.3
   */
  public static float dateDifference(String sDate1, String sDate2, String sFormat, long lBaseMillis) {

    Date oDate1 = toDate(sDate1, sFormat);
    Date oDate2 = toDate(sDate2, sFormat);

    return dateDifference(oDate1, oDate2, lBaseMillis);

  }

  /**
   * Calcola la differenza fra due data/ora in millisecondi, secondi,
   * ore, minuti, giorni.
   *
   * @param sData1 prima data/ora.
   * @param sData1 seconda data/ora.
   * @param lBaseMillis coefficiente di normalizzazione (un valore fra
   *                 {@link #MILLISECONDS_ONE_SECOND},
   *                 {@link #MILLISECONDS_ONE_MINUTE},
   *                 {@link #MILLISECONDS_ONE_HOUR},
   *                 {@link #MILLISECONDS_ONE_DAY}).
   *
   * @return la differenza in valore assoluto.
   *
   * @since  JDK1.3
   */
  public static float dateDifference(Date oDate1, Date oDate2, long lBaseMillis) {

    long lMillis1 = oDate1.getTime();
    long lMillis2 = oDate2.getTime();

    return divide(lMillis1 - lMillis2, lBaseMillis, 2, true);

  }

  /**
   * Calcola il quoziente fra due numeri interi come numero decimale.
   *
   * @param lNumerator   numeratore/dividendo.
   * @param lDenumerator denominatore/divisore.
   * @param lNumDecimals numero di cifre decimali per l'arrotondamento
   *                     del risultato.
   * @param bAbsolut     se <code>true</code> ritorna il valore assoluto,
   *                     se <code>false</code> mantiene il segno.
   *
   * @return il quoziente.
   *
   * @since  JDK1.3
   */
  public static float divide(long lNumerator, long lDenumerator, int iNumDecimals, boolean bAbsolut) {

    BigDecimal oNumerator = BigDecimal.valueOf(lNumerator);
    BigDecimal oDenumerator = BigDecimal.valueOf(lDenumerator);

    BigDecimal oResult = oNumerator.divide(oDenumerator, iNumDecimals, BigDecimal.ROUND_HALF_UP);

    return (bAbsolut) ? oResult.abs().floatValue() : oResult.floatValue();

  }

  /**
   * Calcola la data futura rispetto a quella di partenza sommando giorni, ore, minuti e secondi.
   *
   * @param sDate data/ora di partenza come stringa.
   * @param lDays giorni da sommare.
   * @param lHours ore da sommare.
   * @param lMinutes minuti da sommare.
   * @param lSeconds secondi da sommare.
   * @param sFormat stringa di formato delle due data/ora (un valore fra
   *                 {@link #DATE_FORMAT},
   *                 {@link #DATE_COMPACT_FORMAT},
   *                 {@link #DATE_FILE_FORMAT},
   *                 {@link #DATETIME_FORMAT}).
   *
   * @return la data futura come stringa.
   *
   * @since  JDK1.3
   */
  public static String dateAddition(String sDate, long lDays, long lHours, long lMinutes, long lSeconds, String sFormat) {

    Date oDate = toDate(sDate, sFormat);

    return formatDate(dateAddition(oDate, lDays, lHours, lMinutes, lSeconds), sFormat);

  }

  /**
   * Calcola la data futura rispetto a quella di partenza sommando giorni, ore, minuti e secondi.
   *
   * @param oDate data/ora di partenza.
   * @param lDays giorni da sommare.
   * @param lHours ore da sommare.
   * @param lMinutes minuti da sommare.
   * @param lSeconds secondi da sommare.
   *
   * @return la data futura.
   *
   * @since  JDK1.3
   */
  public static Date dateAddition(Date oDate, long lDays, long lHours, long lMinutes, long lSeconds) {

    long lMillis1 = oDate.getTime();
    long lMillis2 = (lDays * MILLISECONDS_ONE_DAY) 
    			  + (lHours * MILLISECONDS_ONE_HOUR)
    			  + (lMinutes * MILLISECONDS_ONE_MINUTE) 
    			  + (lSeconds * MILLISECONDS_ONE_SECOND);
    return new Date(lMillis1 + lMillis2);
  
  }
  
  /**
   * Crea tutte le directory incluse nel percorso specificato.
   *
   * @param sPath percorso.
   *
   * @exception IOException se la scrittura su disco fallisce.
   *
   * @since  JDK1.3
   */
  public static void createPath(String sPath) throws IOException {

    try {

      File oFile = new File(sPath);

      oFile.mkdirs();

    } catch (Exception oException) {

      throw new IOException("Error creating path " + sPath);

    }

  }

  public static String[] scanPath(String sRootpath, boolean bWithDirectories, boolean bWithFiles, boolean bAbsolute) {

    Vector<String> oPaths = new Vector<String>();

    scanPath(sRootpath.replace(PATH_SEPARATOR, URL_SEPARATOR),
             sRootpath.replace(PATH_SEPARATOR, URL_SEPARATOR),
             "",
             oPaths,
             bWithDirectories,
             bWithFiles,
             bAbsolute);

    return toStringArray(oPaths, true);

  }

  private static void scanPath(String sRootpath, String sScanpath, String sDirectory, Vector<String> oPaths, boolean bWithDirectories, boolean bWithFiles, boolean bAbsolute) {

    File oFile = new File(sScanpath, sDirectory);

    String sPath = oFile.getPath().replace(PATH_SEPARATOR, URL_SEPARATOR);

    if (oFile.isDirectory()) {

      System.out.println("scanPath - Aggiunge la directory " + oFile.getPath());

      if (bWithDirectories) {

        if (bAbsolute) {

          oPaths.add(sPath);

        } else if (sPath.equals(sRootpath)) {

          oPaths.add((new Character(URL_SEPARATOR)).toString());

        } else {

          oPaths.add(StringUtils.replacePattern(sPath, sRootpath, "", true));

        }

      }

      String[] aPaths = oFile.list();

      for (int i = 0; i < aPaths.length; i ++) {

        scanPath(sRootpath,
                 sPath,
                 aPaths[i].replace(PATH_SEPARATOR, URL_SEPARATOR),
                 oPaths,
                 bWithDirectories,
                 bWithFiles,
                 bAbsolute);

      }

    } else if (bWithFiles) {

      System.out.println("scanPath - Aggiunge il file " + oFile.getPath());

      if (bAbsolute) {

        oPaths.add(sPath);

      } else {

        oPaths.add(StringUtils.replacePattern(sPath, sRootpath, "", true));

      }

    }

  }

  /**
   * Copia un testo in un file di ASCII.
   *
   * @param sText     testo.
   * @param sPath     percorso del file.
   * @param sFilename nome del file.s
   *
   * @exception IOException se la scrittura su disco fallisce.
   *
   * @since  JDK1.3
   */
  public static void saveTextOnFile(String sText, String sPath, String sFilename) throws IOException {

    PrintStream oPrintStream = null;

    try {

      File oFile = new File(sPath, sFilename);

      oPrintStream = new PrintStream(new FileOutputStream(oFile));

      oPrintStream.println(sText);

    } catch (Exception oException) {

      throw new IOException("Error saving file " + sFilename);

    } finally {

      if (oPrintStream != null) oPrintStream.close();

    }

  }

  public static void main (String[] args) throws Exception {

    System.out.println("Run splitString() ...");

    String[] aSplits = splitString("0123456789", 3, true, false);

    if (aSplits != null) {

      for (int i = 0; i < aSplits.length; i ++) {

        System.out.println("  Split[" + i + "] = " + aSplits[i]);

      }

    } else {

      System.out.println("  Error running splitString");

    }

    System.out.println("Run replacePattern() ...");

    String sReplaced = replacePattern("BONIFICATO000250000S$$DB &amp;amp; B 00000002187804$$$", "&amp;amp;", "&", true);

    System.out.println("  Replaced string = " + sReplaced);

    System.out.println("Run removePatternRipetition() ...");

    String sRemoved = removePatternRipetition("abbbcc", "b", true);

    System.out.println("  Removed string = " + sRemoved);

    System.out.println();
    System.out.println("Run formatNumber() ...");

    String sNumber1 = formatNumber(-6543210.123456f, 2, NUMBER_SEPARATOR_POINT, NUMBER_SEPARATOR_SPACE, "0.00");

    System.out.println("Format number 1 = " + sNumber1);

    String sNumber2 = formatNumber(null, 2, NUMBER_SEPARATOR_POINT, NUMBER_SEPARATOR_SPACE, "NUMERO DI DEFAULT");

    System.out.println("Format number 2 = " + sNumber2);

    System.out.println();
    System.out.println("Run formatString() ...");

    String sResized1 = formatString("Andrea Fazioni", 20, "#", true);

    System.out.println("  Resized string 1 = [" + sResized1 + "]");

    String sResized2 = formatString("Andrea Fazioni", 7, "", true);

    System.out.println("  Resized string 2 = [" + sResized2 + "]");

    System.out.println();
    System.out.println("Run formatCurrentDate() *** using XML format ...");

    String sCurrentDate = formatCurrentDate(DATETIME_XML_FORMAT);

    System.out.println("  Formatted current date = [" + sCurrentDate + "]");

    System.out.println();
    System.out.println("Run toDate() *** using XML format ...");

    Date oCurrentDate = toDate("1973-03-28T23:30:00", DATETIME_XML_FORMAT);

    System.out.println("  Current date = [" + oCurrentDate.toString() + "]");
    
    System.out.println();
    System.out.println("Run toStringArray() ...");

    Vector<String> oVector = new Vector<String>();

    oVector.add("uno");
    oVector.add("due");
    oVector.add("tre");
    oVector.add("quattro");

    String[] aVector = toStringArray(oVector, true);

    for (int i = 0; i < aVector.length; i ++) {

      System.out.println("  Element[" + i + "] = " + aVector[i]);

    }

    System.out.println();
    System.out.println("Run sortArray() ...");

    String[] aToSort = { "uno", "due", "tre", "quattro" };

    sortArray(aToSort);

    for (int i = 0; i < aToSort.length; i ++) {

      System.out.println("  Element[" + i + "] = " + aToSort[i]);

    }

    System.out.println();
    System.out.println("Run dateDifference() ...");

    float fDateDifference1 = dateDifference("28/03/1973 12:00:00",
                                            "29/03/1973 13:45:00",
                                            StringUtils.DATETIME_FORMAT,
                                            MILLISECONDS_ONE_HOUR);

    System.out.println("  Date difference 1 = " + fDateDifference1 + " hours");

    float fDateDifference2 = dateDifference(toDate("04/09/2003 01:06:00", DATETIME_FORMAT),
                                            new Date(),
                                            MILLISECONDS_ONE_DAY);

    System.out.println("  Date difference 2 = " + fDateDifference2 + " days");

    float fDateDifference3 = dateDifference("28/03/1973 12:00:00",
                                            "28/03/1973 12:01:00",
                                            StringUtils.DATETIME_FORMAT,
                                            MILLISECONDS_ONE_SECOND);

    System.out.println("  Date difference 3 = " + fDateDifference3 + " seconds");

    System.out.println();
    System.out.println("Run createPath() ...");

    createPath("C:/temp/StringUtils_" + formatCurrentDate(DATE_FILE_FORMAT));

    System.out.println();
    System.out.println("Run scanPath() ...");

    String[] aPaths = scanPath("C:/Programmi/jakarta-tomcat-4.1.24/webapps/CCMWeb/doc",
                               true,
                               false,
                               false);

    if (aPaths == null || aPaths.length == 0) {

      System.out.println("  No path found");

    } else {

      for (int i = 1; i < aPaths.length; i ++) { // 1 per escludere la root!!!

        System.out.println("  Path[" + i + "] = " + aPaths[i]);

      }

    }

  }

  /**
   * Punto separatore della parte decimale o delle
   * migliaia per la formattazione dei numeri.
   *
   * @see #formatNumber
   */
  public static final String NUMBER_SEPARATOR_POINT = ".";

  /**
   * Virgola separatore della parte decimale o delle
   * migliaia per la formattazione dei numeri.
   *
   * @see #formatNumber
   */
  public static final String NUMBER_SEPARATOR_COMMA = ",";

  /**
   * Spazio separatore della parte decimale o delle
   * migliaia per la formattazione dei numeri.
   *
   * @see #formatNumber
   */
  public static final String NUMBER_SEPARATOR_SPACE = " ";

  /**
   * Nessun separatore della parte decimale o delle
   * migliaia per la formattazione dei numeri.
   *
   * @see #formatNumber
   */
  public static final String NUMBER_SEPARATOR_NONE = "";

  /**
   * Stringa del formato data esteso DD/MM/YYYY.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String DATE_FORMAT = "dd/MM/yyyy";

  /**
   * Stringa del formato data esteso DDMMYY.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String DATE_SIX_FORMAT = "ddMMyy";

  /**
   * Stringa del formato data ridotto DD/MM.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String DATE_COMPACT_FORMAT = "dd/MM";

  /**
   * Stringa del formato data YYYYMMDD.
   * Utile come elemento da concatenare ai nomi dei file per
   * imporre un criterio di ordinamento lessicografico.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String DATE_FILE_FORMAT = "yyyyMMdd";

  /**
   * Stringa del formato ora consueto HH:Mm:SS.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String TIME_FORMAT = "HH:mm:ss";

  /**
   * Stringa del formato ora ridotto HH:Mm.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String TIME_COMPACT_FORMAT = "HH:mm";

  /**
   * Stringa del formato ora esteso HH:Mm:SS:sss.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String TIME_EXTENDED_FORMAT = "HH:mm:ss:SSS";

  /**
   * Stringa del formato ora HHMmSS.
   * Utile come elemento da concatenare ai nomi dei file per
   * imporre un criterio di ordinamento lessicografico.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String TIME_FILE_FORMAT = "HHmmss";

  /**
   * Stringa del formato ora HHMm.
   * Utile come elemento da concatenare ai nomi dei file per
   * imporre un criterio di ordinamento lessicografico.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String TIME_FOUR_FORMAT = "HHmm";

  /**
   * Stringa del formato ora esteso HHMmSSsss.
   * Utile come elemento da concatenare ai nomi dei file per
   * imporre un criterio di ordinamento lessicografico.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String TIME_EXTENDED_FILE_FORMAT = "HHmmssSSS";

  /**
   * Stringa del formato data/ora completo DD/MM/YYYY HH:Mm:SS.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

  /**
   * Stringa del formato data/ora completo DD/MM/YYYY HH:Mm:SS:sss.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String DATETIME_EXTENDED_FORMAT = "dd/MM/yyyy HH:mm:ss:SSS";

  /**
   * Stringa del formato data/ora completo YYYYMMDD_HHMmSSsss.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String DATETIME_EXTENDED_FILE_FORMAT = "yyyyMMdd_HHmmssSSS";

  /**
   * Stringa del formato data/ora completo YYYY-MM-DDTHH:Mm:SS.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String DATETIME_XML_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * Stringa del formato data/ora completo MMMM YYYY, HH:Mm:SS.
   *
   * @see #formatDate(Date, String)
   * @see #formatCurrentDate(String)
   */
  public static final String DATETIME_ENGLISH_FORMAT = "MMMM dd yyyy, HH:mm:ss";

  /**
   * Numero di millisecondi in un secondo.
   *
   * @see #dateDifference(String, String, String, long)
   * @see #dateDifference(Date, Date, long)
   */
  public static final long MILLISECONDS_ONE_SECOND = 1000;

  /**
   * Numero di millisecondi in un minuto.
   *
   * @see #dateDifference(String, String, String, long)
   * @see #dateDifference(Date, Date, long)
   */
  public static final long MILLISECONDS_ONE_MINUTE = 60000;

  /**
   * Numero di millisecondi in un'ora.
   *
   * @see #dateDifference(String, String, String, long)
   * @see #dateDifference(Date, Date, long)
   */
  public static final long MILLISECONDS_ONE_HOUR = 3600000;

  /**
   * Numero di millisecondi in un giorno.
   *
   * @see #dateDifference(String, String, String, long)
   * @see #dateDifference(Date, Date, long)
   */
  public static final long MILLISECONDS_ONE_DAY = 86400000;

  public static char PATH_SEPARATOR = System.getProperty("file.separator").charAt(0);
  public static char URL_SEPARATOR = '/';
  public static char NULL_SEPARATOR = '\u0000';

}