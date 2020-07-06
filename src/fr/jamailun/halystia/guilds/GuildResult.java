package fr.jamailun.halystia.guilds;

public enum GuildResult {
	/**
	 * Global
	 */
	SUCCESS,
	GUILD_NOT_VALID,
	PLAYER_NOT_HERE,
	/**
	 * Recruting
	 */
	GUILD_FULL,
	ALREADY_HERE,
	/**
	 * Promote
	 */
	IS_ALREADY_MASTER, CAN_ONLY_HAVE_ONE_MASTER, CAN_ONLY_HAVE_RIGHT_ARM,
	/**
	 * Demote
	 */
	IS_ALREADY_MEMBER, MASTER_CANNOT_BE_DEMOTE,
	/**
	 * Tag change
	 */
	TAG_ALREADY_EXISTS, WRONG_TAG_SIZE,
	/**
	 * Permissions en générales
	 */
	NEED_TO_BE_MASTER, NEED_TO_BE_RA, NEED_TO_BE_CAPTAIN,
	/**
	 * 
	 */
	MASTER_CANNOT_LEAVE,
}