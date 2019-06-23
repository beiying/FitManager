//
// Created by beiying on 2019/5/2.
//

#include <libavutil/log.h>
#include <libavformat/avformat.h>

int printMediaInfo(char* mediaFile[])
{
    int ret;
    AVFormatContext *fmt_ctx = NULL;
    av_log_set_level(AV_LOG_INFO);

    av_register_all();
    ret = avformat_open_input(&fmt_ctx, mediaFile, NULL, NULL);
    if (ret < 0) {
        av_log(NULL, AV_LOG_ERROR, "Can't open file: %s\n", av_err2Str(rest));
        return -1;
    }

    av_dump_format(fmt_ctx, 0, mediaFile, 0);//
    avformat_close_input(&fmt_ctx);
    return ret;

}
void adts_header(char* szAdtsHeader, int dataLen) {
    int audio_object_type = 2;
    int sampling_frequency_index = 7;
    int channel_config = 2;
    int adtsLen = dataLen + 7;
    szAdtsHeader[0] = 0xff;  //syncword: 0xfff
    szAdtsHeader[1] = 0xf0;  //syncword: 0xfff
    szAdtsHeader[1] |= (0 << 3);   //MPEG version:
    szAdtsHeader[1] |= (0 << 1);   //Layer: 0

}
/**
 * 抽取音频数据
 * 注意抽取的音频数据并不能直接播放，每个数据包前必须增加adts头信息才能被播放
 *
 * */

int getAudioData(char* mediaFile[], char* saveFile[])
{
    int ret;
    int audio_index;
    AVFormatContext *fmt_ctx = NULL;
    AVPacket pkt = NULL;

    av_log_set_level(AV_LOG_INFO);
    av_register_all();
    ret = avformat_open_input(&fmt_ctx, mediaFile, NULL, NULL);
    if (ret < 0) {
        av_log(NULL, AV_LOG_ERROR, "Can't open file: %s\n", av_err2Str(rest));
        return -1;
    }

    FILE* save_fd = fopen(saveFile, "wb");
    if (!save_fd) {
        av_log(NULL, AV_LOG_ERROR, "Can't open out file\n " );
        avformat_close_input(&fmt_ctx);
        return -1;
    }

    av_dump_format(fmt_ctx, 0, mediaFile, 0);//

    audio_index = av_find_best_stream(fmt_ctx, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0);
    if (ret < 0) {
        av_log(NULL, AV_LOG_ERROR, "Can't find the best stream!\n " );
        avformat_close_input(&fmt_ctx);
        fclose(save_fd);
        return -1;
    }

    av_init_packet(&pkt);
    int len;
    while(av_read_fram(fmt_ctx, &pkt) >= 0){
        if (pkt.stream_index == audio_index) {
            len = fwrite(pkt.data, 1, pkt.size, save_fd);
            if (len != pkt.size) {
                av_log(NULL, AV_LOG_WARNING, "Warning, length of data is not equal size of packet!!\n " );
            }
        }
        av_packet_unref(&pkt);
    }

    avformat_close_input(&fmt_ctx);
    if (save_fd) {
        fclose(save_fd);
    }
    return ret;
}

int getVideoData(char* )

int main(int argc, char* argv[])
{


}