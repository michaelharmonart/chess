package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTeam;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        currentTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        TeamColor teamColor = piece.getTeamColor();
        ArrayList<ChessMove> validMoves = new ArrayList<ChessMove>();
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            ChessBoard moveBoard = new ChessBoard(board);
            moveBoard.movePiece(move);
            if (!isBoardInCheck(moveBoard, teamColor)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("Invalid move: No piece at specified position.");
        }
        TeamColor teamColor = piece.getTeamColor();
        if (teamColor != currentTeam) {
            throw new InvalidMoveException("Invalid move: Not the current team's piece.");
        }

        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move: That move would put the current team in chess.");
        }
        board.movePiece(move);
        currentTeam = (currentTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isBoardInCheck(board, teamColor);
    }

    public boolean isBoardInCheck(ChessBoard board, TeamColor teamColor) {
        HashSet<ChessPosition> opposingPieceAttackPositions = new HashSet<ChessPosition>();
        ChessBoard.BoardSize size = board.getBoardSize();
        ChessPosition kingPosition = null;
        for (int row = 1; row < size.rows() + 1; row++) {
            for (int col = 1; col < size.columns() + 1; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }
                TeamColor pieceColor = piece.getTeamColor();
                if (pieceColor == teamColor) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPosition = position;
                    }
                } else {
                    for (ChessMove move : piece.pieceMoves(board, position)) {
                        opposingPieceAttackPositions.add(move.getEndPosition());
                    }
                }
            }
        }
        if (kingPosition == null) {
            return false;
        }
        return opposingPieceAttackPositions.contains(kingPosition);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        ChessBoard.BoardSize size = board.getBoardSize();
        for (int row = 1; row < size.rows() + 1; row++) {
            for (int col = 1; col < size.columns() + 1; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }
                TeamColor pieceColor = piece.getTeamColor();
                if (pieceColor == teamColor) {
                    for (ChessMove move : validMoves(position)) {
                        ChessBoard moveBoard = new ChessBoard(board);
                        moveBoard.movePiece(move);
                        if (!isBoardInCheck(moveBoard, teamColor)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        ChessBoard.BoardSize size = board.getBoardSize();
        for (int row = 1; row < size.rows() + 1; row++) {
            for (int col = 1; col < size.columns() + 1; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }
                TeamColor pieceColor = piece.getTeamColor();
                if (pieceColor == teamColor) {
                    for (ChessMove move : validMoves(position)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) && currentTeam == chessGame.currentTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), currentTeam);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", currentTeam=" + currentTeam +
                '}';
    }
}
