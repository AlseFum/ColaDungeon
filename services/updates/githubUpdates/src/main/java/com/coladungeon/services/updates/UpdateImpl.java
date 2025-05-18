/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.coladungeon.services.updates;

public class UpdateImpl {

	// Disabled GitHub updates by using a no-op service
	private static UpdateService updateChecker = new NoOpUpdateService();

	public static UpdateService getUpdateService(){
		return updateChecker;
	}

	public static boolean supportsUpdates(){
		return false;  // Changed to false to indicate updates are not supported
	}

	// No-op update service that does nothing for all update operations
	private static class NoOpUpdateService extends UpdateService {
		@Override
		public boolean supportsUpdatePrompts() {
			return false;
		}

		@Override
		public boolean supportsBetaChannel() {
			return false;
		}

		@Override
		public void checkForUpdate(boolean useMetered, boolean includeBetas, UpdateResultCallback callback) {
			if (callback != null) {
				callback.onNoUpdateFound();
			}
		}

		@Override
		public void initializeUpdate(AvailableUpdateData update) {
			// Do nothing
		}

		@Override
		public boolean supportsReviews() {
			return false;
		}

		@Override
		public void initializeReview(ReviewResultCallback callback) {
			if (callback != null) {
				callback.onComplete();
			}
		}

		@Override
		public void openReviewURI() {
			// Do nothing
		}
	}
}
