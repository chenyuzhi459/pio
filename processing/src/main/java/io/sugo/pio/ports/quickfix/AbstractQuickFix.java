//package io.sugo.pio.ports.quickfix;
//
//
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//
//
//public abstract class AbstractQuickFix implements QuickFix {
//
//	private Action action;
//	private int rating;
//	private boolean isInteractive;
//
//	/**
//	 * @param i18nKey
//	 *            is a key referencing an entry in a properties file that defines the action's text
//	 *            and icon.
//	 * @param i18nArgs
//	 *            arguments to pass to the text formatter
//	 */
//	public AbstractQuickFix(int rating, boolean isInteractive, String i18nKey, Object... i18nArgs) {
//		this.isInteractive = isInteractive;
//		this.rating = rating;
//		seti18nKey(i18nKey, i18nArgs);
//	}
//
//	protected void seti18nKey(String i18nKey, Object... i18nArgs) {
//		this.action = new AbstractAction() {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				try {
//					apply();
//				} catch (Exception e) {
//					// TODO: Handle exception by GUI properly
//					JOptionPane.showMessageDialog(null, e.toString(), "Cannot apply quick fix", JOptionPane.ERROR_MESSAGE);
//					e.printStackTrace();
//				}
//			}
//		};
//	}
//
//	@Override
//	public Action getAction() {
//		return action;
//	}
//
//	@Override
//	public int getRating() {
//		return rating;
//	}
//
//	@Override
//	public boolean isInteractive() {
//		return isInteractive;
//	}
//
//	@Override
//	public int compareTo(QuickFix arg0) {
//		return arg0.getRating() - this.rating;
//	}
//
//	@Override
//	public String toString() {
//		return (String) action.getValue(Action.NAME);
//	}
//}
