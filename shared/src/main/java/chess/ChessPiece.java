package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        return switch (piece.getPieceType()) {
            case PieceType.BISHOP -> bishopMoves(board, myPosition);
            case PieceType.KING -> kingMoves(board, myPosition);
            case PieceType.KNIGHT -> knightMoves(board, myPosition);
            case PieceType.QUEEN -> queenMoves(board, myPosition);
            case PieceType.ROOK -> rookMoves(board, myPosition);
            case PieceType.PAWN -> pawnMoves(board, myPosition);
        };
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition initialPosition) {
        return getDirectionalMoves(board, initialPosition, new int[][]{{1, -1}, {1, 1}, {-1, -1}, {-1, 1}});
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition initialPosition) {
        return getStepMoves(board, initialPosition, new int[][]{{1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}});
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition initialPosition) {
        return getStepMoves(board, initialPosition, new int[][]{{1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}});
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition initialPosition) {
        return getDirectionalMoves(board, initialPosition, new int[][]{{1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}});
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition initialPosition) {
        return getDirectionalMoves(board, initialPosition, new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}});
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition initialPosition) {
        List<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(initialPosition);
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        int[][] moveOffsets = switch (teamColor) {
            case ChessGame.TeamColor.WHITE -> new int[][]{{1, 0}, {1, -1}, {1, 1}};
            case ChessGame.TeamColor.BLACK -> new int[][]{{-1, 0}, {-1, -1}, {-1, 1}};
        };

        int initialRow = switch (teamColor) {
            case ChessGame.TeamColor.WHITE -> 2;
            case ChessGame.TeamColor.BLACK -> 7;
        };

        int[] doubleMoveOffset = switch (teamColor) {
            case ChessGame.TeamColor.WHITE -> new int[]{2, 0};
            case ChessGame.TeamColor.BLACK -> new int[]{-2, 0};
        };


        int promotionRow = switch (teamColor) {
            case ChessGame.TeamColor.WHITE -> 8;
            case ChessGame.TeamColor.BLACK -> 1;
        };

        for (int[] moveOffset : moveOffsets) {
            ChessPosition targetPosition = new ChessPosition(initialPosition.getRow() + moveOffset[0], initialPosition.getColumn() + moveOffset[1]);
            if (!board.isValidSquare(targetPosition)) {
                continue;
            }
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece == null) {
                if (moveOffset[1] != 0) {
                    continue;
                }
            } else {
                if (targetPiece.getTeamColor() == teamColor) {
                    continue;
                }
                if (moveOffset[1] == 0) {
                    continue;
                }
            }

            if (targetPosition.getRow() == promotionRow) {
                for (PieceType type : new PieceType[]{PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT}) {
                    validMoves.add(new ChessMove(initialPosition, targetPosition, type));
                }
            } else {
                validMoves.add(new ChessMove(initialPosition, targetPosition, null));
                if (moveOffset[1] == 0) {
                    if (initialPosition.getRow() == initialRow) {
                        targetPosition = new ChessPosition(initialPosition.getRow() + doubleMoveOffset[0], initialPosition.getColumn() + doubleMoveOffset[1]);
                        targetPiece = board.getPiece(targetPosition);
                        if (targetPiece == null) {
                            validMoves.add(new ChessMove(initialPosition, targetPosition, null));
                        }
                    }
                }

            }
        }
        return validMoves;
    }


    private Collection<ChessMove> getDirectionalMoves(ChessBoard board, ChessPosition initialPosition, int[][] directions) {
        List<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(initialPosition);
        ChessGame.TeamColor teamColor = piece.getTeamColor();
        for (int[] direction : directions) {
            ChessPosition targetPosition = new ChessPosition(initialPosition.getRow(), initialPosition.getColumn());
            while (true) {
                targetPosition = new ChessPosition(targetPosition.getRow() + direction[0], targetPosition.getColumn() + direction[1]);
                if (!board.isValidSquare(targetPosition)) {
                    break;
                }
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece != null) {
                    if (targetPiece.getTeamColor() != teamColor) {
                        validMoves.add(new ChessMove(initialPosition, targetPosition, null));
                        break;
                    } else {
                        break;
                    }
                }
                validMoves.add(new ChessMove(initialPosition, targetPosition, null));
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> getStepMoves(ChessBoard board, ChessPosition initialPosition, int[][] stepOffsets) {
        List<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(initialPosition);
        ChessGame.TeamColor teamColor = piece.getTeamColor();
        for (int[] stepOffset : stepOffsets) {
            ChessPosition targetPosition = new ChessPosition(initialPosition.getRow() + stepOffset[0], initialPosition.getColumn() + stepOffset[1]);
            if (!board.isValidSquare(targetPosition)) {
                continue;
            }
            ChessPiece targetPiece = board.getPiece(targetPosition);
            if (targetPiece != null) {
                if (targetPiece.getTeamColor() == teamColor) {
                    continue;
                }
            }
            validMoves.add(new ChessMove(initialPosition, targetPosition, null));
        }
        return validMoves;
    }
}