/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2024，所有权利保留。
 * <p>
 * 项目名： GitJumpToBrowse
 * 文件名： BaseListPopupElement.java
 * 模块说明：
 * 修改历史：
 * 2024年08月06日 - lixiaoyu - 创建。
 */
package com.git.jumptobrowse.util;

/**
 * @author lixiaoyu
 */
public class BaseListPopupElement {

  private String value;
  private String text;

  public String getValue() {
    return value;
  }

  public BaseListPopupElement setValue(String value) {
    this.value = value;
    return this;
  }

  public String getText() {
    return text;
  }

  public BaseListPopupElement setText(String text) {
    this.text = text;
    return this;
  }

  @Override
  public String toString() {
    return this.text;
  }
}