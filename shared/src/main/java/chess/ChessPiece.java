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
        if (piece.getPieceType() == PieceType.BISHOP) {
            return getDirectionalMoves(board, myPosition, new int[][]{{1, -1}, {1, 1}, {-1, -1}, {-1, 1}});
        }
        if (piece.getPieceType() == PieceType.KNIGHT) {
            return getStepMoves(board, myPosition, new int[][]{{1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}});
        }
        return List.of();
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