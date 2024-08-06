import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Message } from 'primeng/api';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  INIT_STATE:string ="INIT";
  private send:BehaviorSubject<Message> = new BehaviorSubject<Message>({summary:this.INIT_STATE});
  sendSubjectObservable:Observable<Message> = this.send.asObservable()

  public sendMessage(message:Message):void{
    this.send.next(message);
  }
}
