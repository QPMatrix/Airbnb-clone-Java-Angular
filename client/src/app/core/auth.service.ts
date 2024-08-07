import { computed, inject, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { HttpClient, HttpParams, HttpStatusCode } from '@angular/common/http';
import { Location } from '@angular/common';
import { User } from './model/user.model';
import { State } from './model/state.model';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  http: HttpClient = inject(HttpClient);
  location: Location = inject(Location);
  notConnected: string = 'NOT_CONNECTED';
  private fetchUserSignal: WritableSignal<State<User>> = signal(State.Builder<User>().forSuccess({ email: this.notConnected }));
  fetchUser = computed(() => this.fetchUserSignal());

  fetch(forceReSync: boolean): void {
    this.fetchHttpUser(forceReSync)
      .subscribe({
        next: user => this.fetchUserSignal.set(State.Builder<User>().forSuccess(user)),
        error: err => {
          if (err.status === HttpStatusCode.Unauthorized && this.isAuthenticated()) {
            this.fetchUserSignal.set(State.Builder<User>().forSuccess({ email: this.notConnected }));
          } else {
            this.fetchUserSignal.set(State.Builder<User>().forError(err));
          }
        },
      });
  }

  login(): void {
    location.href = `${location.origin}${this.location.prepareExternalUrl('oauth2/authorization/okta')}`;
  }

  logout(): void {
    this.http.post(`${environment.API_URL}/auth/logout`, {})
      .subscribe(
        {
          next: (res: any) => {
            this.fetchUserSignal.set(State.Builder<User>().forSuccess({ email: this.notConnected })),
              location.href = res.logoutUrl;
          },
        },
      );
  }

  isAuthenticated(): boolean {
    if (this.fetchUserSignal().value) {
      return this.fetchUserSignal().value!.email !== this.notConnected;
    } else {
      return false;
    }
  }

  fetchHttpUser(forceReSync: boolean): Observable<User> {
    const parmas = new HttpParams().set('forceReSync', forceReSync);
    return this.http.get<User>(`${environment.API_URL}/auth/get-authenticated-user`);
  }

  hasAnyAuthority(authorities: string[] | string): boolean {
    if (this.fetchUserSignal().value?.email === this.notConnected) {
      return false;
    }
    if (!Array.isArray(authorities)) {
      authorities = [authorities];
    }
    return this.fetchUserSignal().value!.authorities!.some((authority) => authorities.includes(authority));
  }
}
