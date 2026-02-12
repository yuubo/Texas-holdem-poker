package org.example.client.print;

import org.example.common.enume.*;
import org.example.common.init.MessageSourceUtils;
import org.example.common.message.*;

public class ExportConsole {

    private static Print print = new Print();

    private static final String LINE = "-----------------------------------------------------------------------------------------------";

    public static void print(Operate operate) {
        print.print(PrintColorEnum.GREEN, MessageSourceUtils.getMessage("service.gameround.print.hint.a"));
        for (Integer allowOperate : operate.getAllowOperates()) {
            print.print(PrintColorEnum.GREEN, "@" + allowOperate + ":");
            print.print(PrintColorEnum.CYAN, OperateEnum.getOperate(allowOperate).getExplain());
            print.print("  ");
        }
        print.println("");
    }

    public static void println(Message message) {
        if (message.getUser() == null) {
            print.print(PrintColorEnum.RED, MessageSourceUtils.getMessage("service.gameround.print.hint.b"))
                    .println(message.getMessage());
        } else {
            print.print(PrintColorEnum.BLUE, message.getUser().getName())
                    .print(PrintColorEnum.BLUE, ":")
                    .println(message.getMessage());
        }
    }

    public static void print(Player player) {
        GameRound gameRound = player.getGameRound();
        print.println(PrintColorEnum.GREEN, LINE);
        print.print(MessageSourceUtils.getMessage("service.gameround.print.hint.c"))
                .print(gameRound.getScoreTotal()).print(" ")
                .print(MessageSourceUtils.getMessage("service.gameround.print.hint.d"))
                .print(gameRound.getScore()).print(" ");

        if (gameRound.getCommonPokerList() != null && !gameRound.getCommonPokerList().isEmpty()) {
            print.print(MessageSourceUtils.getMessage("service.gameround.print.hint.e"));
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
        print.print(MessageSourceUtils.getMessage("service.gameround.print.hint.f"));
        String tag = isMe ? MessageSourceUtils.getMessage("service.gameround.print.hint.g") : "    ";
        print.print(color,pl.getUser().getName() + tag).print(" ");
        //print.print("状态: ").print(PlayerStatusEnum.getName(pl.getStatus())).print(" ");
        print.print(MessageSourceUtils.getMessage("service.gameround.print.hint.h"))
                .print(pl.getScoreTotal()).print(" ")
                .print(MessageSourceUtils.getMessage("service.gameround.print.hint.i"))
                .print(pl.getScore());

        if (pl.getStatus() == PlayerStatusEnum.BIG_BLIND.getStatus()) {
            print.print(PrintColorEnum.GREEN, MessageSourceUtils.getMessage("service.gameround.print.hint.j"));
        } else if (pl.getStatus() == PlayerStatusEnum.SMALL_BLIND.getStatus()) {
            print.print(PrintColorEnum.CYAN, MessageSourceUtils.getMessage("service.gameround.print.hint.k"));
        }
        print.print(" ");
        if (pl.getPokers() != null && !pl.getPokers().isEmpty()) {
            print.print(MessageSourceUtils.getMessage("service.gameround.print.hint.l"));
            for (Poker poker : pl.getPokers()) {
                print.print(suitsColor(poker.getSuits()))
                        .print(PokerNoEnum.getValue(poker.getNo())).print(" ");
            }
        }

        if (gameRound.getStatus() == GameRoundStatusEnum.FINISH.getStatus()) {
            if (pl.getPartyWinScore() > 0) {
                print.print(PrintColorEnum.RED, PokerTypeEnum.getPokerTypeEnum(pl.getPokerType()));
            } else if (pl.getStatus() != PlayerStatusEnum.FOLD.getStatus()) {
                print.print(PrintColorEnum.CYAN, PokerTypeEnum.getPokerTypeEnum(pl.getPokerType()));
            }

            if (pl.getWinPokers() != null && !pl.getWinPokers().isEmpty()) {
                for (Poker poker : pl.getWinPokers()) {
                    print.print(suitsColor(poker.getSuits()))
                            .print(PokerNoEnum.getValue(poker.getNo()))
                            .print(" ");
                }
            }

            print.print(" ");

            if (pl.getPartyWinScore() > 0) {
                print.print(PrintColorEnum.RED, MessageSourceUtils.getMessage("service.gameround.print.hint.m"))
                        .print(PrintColorEnum.RED, pl.getPartyWinScore());
            }

        } else {
            if (pl.getStatus() == PlayerStatusEnum.FILL.getStatus()) {
                print.print(PrintColorEnum.GREEN, MessageSourceUtils.getMessage("service.gameround.print.hint.n"));
            } else if (pl.getStatus() == PlayerStatusEnum.ALL_IN.getStatus()) {
                print.print(PrintColorEnum.GREEN, MessageSourceUtils.getMessage("service.gameround.print.hint.o"));
            } else if (pl.getStatus() == PlayerStatusEnum.FOLD.getStatus()) {
                print.print(PrintColorEnum.GREEN, MessageSourceUtils.getMessage("service.gameround.print.hint.p"));
            } else if (pl.getStatus() == PlayerStatusEnum.PASS.getStatus()) {
                print.print(PrintColorEnum.GREEN, MessageSourceUtils.getMessage("service.gameround.print.hint.q"));
            } else if (pl.getStatus() == PlayerStatusEnum.CALL.getStatus()) {
                print.print(PrintColorEnum.GREEN, MessageSourceUtils.getMessage("service.gameround.print.hint.r"));
            } else if (pl.getStatus() == PlayerStatusEnum.ONLOOKER.getStatus()) {
                print.print(MessageSourceUtils.getMessage("service.gameround.print.hint.s"));
            }
        }
        print.print(" ");

        if (pl.getActivity() == PlayerActivityEnum.ACTIVITY.getNumber()) {
            print.print(PrintColorEnum.RED, MessageSourceUtils.getMessage("service.gameround.print.hint.t"));
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
