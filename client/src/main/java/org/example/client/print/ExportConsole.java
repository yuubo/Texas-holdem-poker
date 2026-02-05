package org.example.client.print;

import org.example.common.bo.*;
import org.example.common.enume.*;

public class ExportConsole {

    private static Print print = new Print();

    private static final String LINE = "------------------------------------------------------------------";

    public static void print(Operate operate) {
        print.print(PrintColorEnum.GREEN, "允许的操作：");
        for (Integer allowOperate : operate.getAllowOperates()) {
            print.print(PrintColorEnum.GREEN, "@" + allowOperate + ":");
            print.print(PrintColorEnum.CYAN, OperateEnum.getOperate(allowOperate).getExplain());
            print.print("  ");
        }
        print.println("");
    }

    public static void println(Message message) {
        if (message.getUser() == null) {
            print.println(PrintColorEnum.RED, "系统:" + message.getMessage());
        } else {
            print.println(PrintColorEnum.BLUE, message.getUser().getName()+":"+message.getMessage());
        }
    }

    public static void print(Player player) {
        StringBuffer stringBuffer = new StringBuffer();
        GameRound gameRound = player.getGameRound();
        print.println(PrintColorEnum.GREEN, LINE);
        print.print("注池: ").print(gameRound.getScoreTotal()).print(" ")
                .print("最高注: ").print(gameRound.getScore()).print(" ");
        if (gameRound.getCommonPokerList() != null && !gameRound.getCommonPokerList().isEmpty()) {
            print.print("公共牌: ");
            for (Poker poker : gameRound.getCommonPokerList()) {
                print.print(suitsColor(poker.getSuits()))
                        .print(PokerNoEnum.getValue(poker.getNo())).print(" ");
            }
        }
        print.println("");
        print.println(PrintColorEnum.GREEN, LINE);

        for (int i = 0; i < gameRound.getPlayerList().size(); i++) {
            Player pl = gameRound.getPlayerList().get(i);
            PrintColorEnum color = PrintColorEnum.YELLOW;
            boolean isMe = false;
            if (pl == player) {
                color = PrintColorEnum.PURPLE;
                isMe = true;
            }

            printPlayer(pl, color, isMe, player.getGameRound());
        }

    }

    public static String suitsColor(int suits) {
        if (suits == PokerSuitsEnum.DIAMOND.getNumber() || suits == PokerSuitsEnum.HEART.getNumber()) {
            return PrintColorEnum.RED.getValue() + PokerSuitsEnum.getValue(suits) + PrintColorEnum.RESET.getValue();
        }
        return PokerSuitsEnum.getValue(suits);
    }

    public static void printPlayer(Player pl, PrintColorEnum color, boolean isMe, GameRound gameRound) {
        print.print("玩家: ");
        String tag = isMe ? "(我)" : "    ";
        print.print(color,pl.getUser().getName() + tag).print(" ");
        //print.print("状态: ").print(PlayerStatusEnum.getName(pl.getStatus())).print(" ");
        print.print("总分: ").print(pl.getScoreTotal()).print(" ")
                .print("下注: ").print(pl.getScore());
        if (pl.getStatus() == PlayerStatusEnum.BIG_BLIND.getStatus()) {
            print.print(PrintColorEnum.GREEN, "(大盲注)");
        } else if (pl.getStatus() == PlayerStatusEnum.SMALL_BLIND.getStatus()) {
            print.print(PrintColorEnum.CYAN, "(小盲注)");
        }
        print.print(" ");
        if (pl.getPokers() != null && !pl.getPokers().isEmpty()) {
            print.print("手牌: ");
            for (Poker poker : pl.getPokers()) {
                print.print(suitsColor(poker.getSuits()))
                        .print(PokerNoEnum.getValue(poker.getNo())).print(" ");
            }
        }

        if (gameRound.getStatus() == GameRoundStatusEnum.FINISH.getStatus()) {
            if (pl.getPartyWinScore() > 0) {
                print.print(PrintColorEnum.RED, PokerTypeEnum.getPokerTypeEnum(pl.getPokerType()))
                        .print(PrintColorEnum.RED, "获胜,获得积分: ")
                        .print(PrintColorEnum.RED, pl.getPartyWinScore());
            } else if (pl.getStatus() != PlayerStatusEnum.FOLD.getStatus()) {
                print.print(PrintColorEnum.CYAN, PokerTypeEnum.getPokerTypeEnum(pl.getPokerType()));
            }
        } else {
            if (pl.getStatus() == PlayerStatusEnum.FILL.getStatus()) {
                print.print(PrintColorEnum.GREEN, "加注");
            } else if (pl.getStatus() == PlayerStatusEnum.ALL_IN.getStatus()) {
                print.print(PrintColorEnum.GREEN, "all-in");
            } else if (pl.getStatus() == PlayerStatusEnum.FOLD.getStatus()) {
                print.print(PrintColorEnum.GREEN, "弃牌");
            } else if (pl.getStatus() == PlayerStatusEnum.PASS.getStatus()) {
                print.print(PrintColorEnum.GREEN, "过牌");
            } else if (pl.getStatus() == PlayerStatusEnum.CALL.getStatus()) {
                print.print(PrintColorEnum.GREEN, "跟注");
            }
        }
        print.print(" ");

        if (pl.getActivity() == PlayerActivityEnum.ACTIVITY.getNumber()) {
            print.print(PrintColorEnum.RED, "等待下注");
        }
        print.println("");

        print.println(color, LINE);
    }

    private static class Print {

        public Print print(Object context) {
            return print(null, context);
        }

        public Print print(PrintColorEnum color, Object context) {
            if (color == null) {
                System.out.print(context);
            } else {
                System.out.print(color.getValue() + context + PrintColorEnum.RESET.getValue());
            }
            return this;
        }

        public Print println(Object context) {
            return println(null, context);
        }

        public Print println(PrintColorEnum color, Object context) {
            if (color == null) {
                System.out.println(context);
            } else {
                System.out.println(color.getValue() + context + PrintColorEnum.RESET.getValue());
            }
            return this;
        }
    }
}
