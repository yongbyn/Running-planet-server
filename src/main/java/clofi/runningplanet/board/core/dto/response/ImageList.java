package clofi.runningplanet.board.core.dto.response;

import clofi.runningplanet.board.domain.BoardImage;

public record ImageList (
	Long id,
	String img
){
	public static ImageList of(BoardImage boardImage) {
		return new ImageList(boardImage.getId(), boardImage.getImageUrl());
	}
}
